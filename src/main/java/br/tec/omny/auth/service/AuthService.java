package br.tec.omny.auth.service;

import br.tec.omny.auth.dto.AdminLoginRequest;
import br.tec.omny.auth.dto.AdminLoginResponse;
import br.tec.omny.auth.dto.ClientInfoResponse;
import br.tec.omny.auth.dto.LoginRequest;
import br.tec.omny.auth.dto.RegisterRequest;
import br.tec.omny.auth.dto.SiteRegisterRequest;
import br.tec.omny.auth.entity.Client;
import br.tec.omny.auth.entity.Contact;
import br.tec.omny.auth.entity.Project;
import br.tec.omny.auth.entity.Site;
import br.tec.omny.auth.entity.Staff;
import br.tec.omny.auth.entity.Task;
import br.tec.omny.auth.entity.Warehouse;
import br.tec.omny.auth.repository.ClientRepository;
import br.tec.omny.auth.repository.ContactRepository;
import br.tec.omny.auth.repository.ProjectRepository;
import br.tec.omny.auth.repository.SiteRepository;
import br.tec.omny.auth.repository.StaffRepository;
import br.tec.omny.auth.repository.TaskRepository;
import br.tec.omny.auth.repository.WarehouseRepository;
import br.tec.omny.auth.repository.SiteImageRepository;
import br.tec.omny.auth.util.JwtUtil;
import br.tec.omny.auth.util.PasswordHash;
import br.tec.omny.auth.service.RecaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronization;
import java.util.concurrent.CompletableFuture;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AuthService {
    
    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private ContactRepository contactRepository;
    
    @Autowired
    private StaffRepository staffRepository;
    
    @Autowired
    private WarehouseRepository warehouseRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private SiteRepository siteRepository;
    
    @Autowired
    private SiteImageRepository siteImageRepository;

    @Autowired
    private FileUploadService fileUploadService;
    
    @Autowired
    private QueueService queueService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private RecaptchaService recaptchaService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Value("${app.site.max-sites-per-client:3}")
    private int maxSitesPerClient;
    
    private final PasswordHash passwordHash = new PasswordHash(8, false);
    
    /**
     * Registra um novo usuário
     * @param request Dados do registro
     * @return Cliente criado
     * @throws Exception Se houver erro no registro
     */
    public Client register(RegisterRequest request) throws Exception {
        // Valida reCAPTCHA se estiver habilitado
        if (recaptchaService.isEnabled()) {
            if (!recaptchaService.validateRecaptcha(request.getRecaptchaToken())) {
                throw new Exception("reCAPTCHA inválido. Por favor, tente novamente.");
            }
        }
        
        // Verifica se o email já existe
        if (contactRepository.existsByEmail(request.getEmail())) {
            throw new Exception("Email já está em uso");
        }
        
        // Cria o cliente
        Client client = new Client(
            request.getCompany(),
            request.getPhoneNumber(),
            request.getZip(),
            request.getCity(),
            request.getState(),
            request.getAddress()
        );
        
        // Salva o cliente
        client = clientRepository.save(client);
        
        // Valida se a senha foi fornecida
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new Exception("Senha é obrigatória");
        }
        
        // Cria o contato
        Contact contact = new Contact(
            client.getUserId(),
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getPhoneNumber(),
            passwordHash.hashPassword(request.getPassword())
        );
        
        // Define como contato primário
        contact.setIsPrimary(true);
        
        // Salva o contato
        contact = contactRepository.save(contact);
        
        // Verifica se o cadastro foi salvo com sucesso antes de enviar email
        if (contact.getId() != null) {
            // Agenda o email para ser enviado APÓS o commit da transação
            scheduleEmailAfterCommit(contact.getId());
        }
        
        return client;
    }
    
    /**
     * Autentica um usuário
     * @param request Dados do login
     * @return Contato autenticado
     * @throws Exception Se as credenciais estiverem incorretas
     */
    public Contact login(LoginRequest request) throws Exception {
        // Busca o contato por email
        Optional<Contact> contactOpt = contactRepository.findByEmail(request.getEmail());
        
        if (contactOpt.isEmpty()) {
            throw new Exception("Email não encontrado");
        }
        
        Contact contact = contactOpt.get();
        
        // Verifica a senha
        if (!passwordHash.checkPassword(request.getPassword(), contact.getPassword())) {
            throw new Exception("Email ou senha incorretos");
        }
        
        return contact;
    }
    
    /**
     * Inicia processo de recuperação de senha
     * @param email Email do usuário
     * @return true se o email foi encontrado
     */
    public boolean initiatePasswordRecovery(String email) {
        Optional<Contact> contactOpt = contactRepository.findByEmail(email);
        
        if (contactOpt.isPresent()) {
            // Aqui você pode implementar o envio de email
            // Por enquanto, apenas retorna true se o email existe
            return true;
        }
        
        return false;
    }
    
    /**
     * Autentica um administrador/staff
     * @param request Dados do login admin
     * @return Resposta do login admin
     * @throws Exception Se as credenciais estiverem incorretas
     */
    public AdminLoginResponse adminLogin(AdminLoginRequest request) throws Exception {
        // Busca o staff por email
        Optional<Staff> staffOpt = staffRepository.findByEmail(request.getEmail());
        
        if (staffOpt.isEmpty()) {
            return AdminLoginResponse.error("Usuário inexistente");
        }
        
        Staff staff = staffOpt.get();
        
        // Busca o warehouse
        Optional<Warehouse> warehouseOpt = warehouseRepository.findByWarehouseId(request.getWarehouseId());
        if (warehouseOpt.isEmpty()) {
            return AdminLoginResponse.error("Warehouse não encontrado");
        }
        
        Warehouse warehouse = warehouseOpt.get();
        
        // Verifica se o usuário não é admin e tem acesso ao warehouse
        if (staff.getAdmin() != 1) {
            String warehouseJson = staff.getWarehouse();
            if (warehouseJson != null && !warehouseJson.isEmpty()) {
                try {
                    // Parse do JSON dos warehouses permitidos
                    String[] warehouseIds = warehouseJson.replaceAll("[\\[\\]\"]", "").split(",");
                    List<String> allowedWarehouses = Arrays.asList(warehouseIds);
                    
                    if (!allowedWarehouses.contains(request.getWarehouseId().toString())) {
                        return AdminLoginResponse.error("Acesso negado a loja selecionada.");
                    }
                } catch (Exception e) {
                    return AdminLoginResponse.error("Erro ao verificar permissões de warehouse");
                }
            }
        }
        
        // Verifica a senha
        if (!passwordHash.checkPassword(request.getPassword(), staff.getPassword())) {
            return AdminLoginResponse.error("Login Inválido");
        }
        
        // Verifica se o usuário está ativo
        if (staff.getActive() == 0) {
            return AdminLoginResponse.error("Usuário inativo, contate o administrador");
        }
        
        // Remove dados sensíveis do staff
        staff.setPassword(null);
        staff.setFacebook(null);
        staff.setSkype(null);
        staff.setLastActivity(null);
        staff.setNewPassKey(null);
        staff.setNewPassKeyRequested(null);
        staff.setDirection(null);
        staff.setHourlyRate(null);
        staff.setEmailSignature(null);
        staff.setPerfexSaasTenantId(null);
        
        // Gera o token JWT
        String token = jwtUtil.generateToken(
            staff.getStaffId(),
            staff.getAdmin(),
            staff.getStaffId(),
            staff.getFirstName(),
            staff.getLastName(),
            staff.getPhoneNumber()
        );
        
        return AdminLoginResponse.success("Logado com sucesso", staff, token, warehouse);
    }
    
    /**
     * Registra um novo site com cliente, contato, projeto e task
     * @param request Dados do registro do site
     * @return Cliente criado
     * @throws Exception Se houver erro no registro
     */
    public Client registerSite(SiteRegisterRequest request) throws Exception {
        // Valida reCAPTCHA apenas se for cadastro novo (sem user_id) e se estiver habilitado
        if (recaptchaService.isEnabled() && request.getUserId() == null) {
            if (!recaptchaService.validateRecaptcha(request.getRecaptchaToken())) {
                throw new Exception("reCAPTCHA inválido. Por favor, tente novamente.");
            }
        }
        
        Client client;
        Contact contact;
        
        // Validações baseadas no preference
        if ("descricao".equals(request.getPreference())) {
            // Se preference == "descricao", description_site é obrigatório
            if (request.getDescriptionSite() == null || request.getDescriptionSite().trim().isEmpty()) {
                throw new Exception("Descrição do site é obrigatória quando preference é 'descricao'");
            }
        }
        
        // Validação de password obrigatório apenas quando não existe user_id
        if (request.getUserId() == null) {
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                throw new Exception("Senha é obrigatória para novos clientes");
            }
        }
        // Se user_id existe, não valida nem altera a senha
        
        // Verifica se user_id foi fornecido
        if (request.getUserId() != null) {
            // Valida se o cliente existe
            Optional<Client> existingClientOpt = clientRepository.findById(request.getUserId());
            if (existingClientOpt.isPresent()) {
                // Cliente existe - usa o cliente existente
                client = existingClientOpt.get();
                
                // Se user_id existe, firstName, lastName, phoneNumber e email são obrigatórios
                if (request.getFirstname() == null || request.getFirstname().trim().isEmpty()) {
                    throw new Exception("Primeiro nome é obrigatório quando user_id é fornecido");
                }
                if (request.getLastname() == null || request.getLastname().trim().isEmpty()) {
                    throw new Exception("Último nome é obrigatório quando user_id é fornecido");
                }
                if (request.getPhonenumber() == null || request.getPhonenumber().trim().isEmpty()) {
                    throw new Exception("Telefone é obrigatório quando user_id é fornecido");
                }
                if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                    throw new Exception("Email é obrigatório quando user_id é fornecido");
                }
                
                // Atualiza o company com a concatenação de firstName e lastName
                client.setCompany(request.getFirstname() + " " + request.getLastname());
                client.setPhoneNumber(request.getPhonenumber());
                
                // Salva as alterações do cliente
                client = clientRepository.save(client);
                
                // Busca o contato primário existente do cliente
                Optional<Contact> existingContactOpt = contactRepository.findByUserIdAndIsPrimary(client.getUserId(), true);
                if (existingContactOpt.isPresent()) {
                    // Atualiza o contato existente (sem alterar a senha)
                    contact = existingContactOpt.get();
                    contact.setFirstName(request.getFirstname());
                    contact.setLastName(request.getLastname());
                    contact.setEmail(request.getEmail());
                    contact.setPhoneNumber(request.getPhonenumber());
                    // NÃO altera a senha - mantém a senha existente
                    
                    // Salva as alterações do contato
                    contact = contactRepository.save(contact);
                } else {
                    // Se não existe contato primário, cria um novo (caso raro)
                    contact = new Contact();
                    contact.setUserId(client.getUserId());
                    contact.setFirstName(request.getFirstname());
                    contact.setLastName(request.getLastname());
                    contact.setEmail(request.getEmail());
                    contact.setPhoneNumber(request.getPhonenumber());
                    contact.setIsPrimary(true);
                    contact.setPassword(passwordHash.hashPassword(request.getPassword())); // Senha padrão apenas se não existir contato
                    
                    // Salva o contato
                    contact = contactRepository.save(contact);
                }
                
            } else {
                // Cliente não existe - cria um novo cliente
                // Verifica se o email já existe
                if (request.getEmail() != null && contactRepository.existsByEmail(request.getEmail())) {
                    throw new Exception("Email já está em uso");
                }
                
                // Cria o cliente
                client = new Client();
                client.setCompany(request.getCompany() != null ? request.getCompany() : 
                    (request.getFirstname() != null && request.getLastname() != null ? 
                        request.getFirstname() + " " + request.getLastname() : "Nova Empresa"));
                client.setPhoneNumber(request.getPhonenumber());
                client.setActive(true);
                client.setDefaultClient(true);

                // Salva o cliente
                client = clientRepository.save(client);
                
                // Cria o contato
                contact = new Contact();
                contact.setUserId(client.getUserId());
                contact.setFirstName(request.getFirstname());
                contact.setLastName(request.getLastname());
                contact.setEmail(request.getEmail());
                contact.setPhoneNumber(request.getPhonenumber());
                contact.setIsPrimary(true);
                contact.setPassword(passwordHash.hashPassword(request.getPassword())); // Senha fornecida pelo usuário
                
                // Salva o contato
                contact = contactRepository.save(contact);
                
                // Verifica se o cadastro foi salvo com sucesso antes de enviar email
                if (contact.getId() != null) {
                    // Agenda o email para ser enviado APÓS o commit da transação
                    scheduleEmailAfterCommit(contact.getId());
                }
            }
        } else {
            // Lógica original para novo cliente
            // Verifica se o email já existe
            if (request.getEmail() != null && contactRepository.existsByEmail(request.getEmail())) {
                throw new Exception("Email já está em uso");
            }
            
            // Cria o cliente
            client = new Client();
            client.setCompany(request.getCompany());
            client.setPhoneNumber(request.getPhonenumber());
            client.setActive(true);
            client.setDefaultClient(true);

            // Salva o cliente
            client = clientRepository.save(client);
            
            // Cria o contato
            contact = new Contact();
            contact.setUserId(client.getUserId());
            contact.setFirstName(request.getFirstname());
            contact.setLastName(request.getLastname());
            contact.setEmail(request.getEmail());
            contact.setPhoneNumber(request.getPhonenumber());
            contact.setIsPrimary(true);
            contact.setPassword(passwordHash.hashPassword(request.getPassword())); // Senha fornecida pelo usuário
            
            // Salva o contato
            contact = contactRepository.save(contact);
            
            // Verifica se o cadastro foi salvo com sucesso antes de enviar email
            if (contact.getId() != null) {
                // Agenda o email para ser enviado APÓS o commit da transação
                scheduleEmailAfterCommit(contact.getId());
            }
        }
        
        // Valida limite de sites por cliente
        long currentSitesCount = siteRepository.countByClientId(client.getUserId().intValue());
        if (currentSitesCount >= maxSitesPerClient) {
            throw new Exception("Limite de sites excedido. Cada cliente pode criar no máximo " + maxSitesPerClient + " sites. Cliente atual possui " + currentSitesCount + " sites.");
        }
        
        // Cria o site
        Site site = new Site();
        site.setClientId(client.getUserId().intValue());
        site.setNomeSite(request.getNomeSite());
        site.setDominio(request.getDominio());
        site.setTipoSite(request.getTipoSite());
        site.setDescricaoNegocio(request.getDescricaoNegocio());
        site.setPublicoAlvo(request.getPublicoAlvo());
        site.setBannerTexto(request.getBannerTexto());
        
        // Novos campos
        site.setPreference(request.getPreference());
        site.setDescriptionSite(request.getDescriptionSite());
        site.setTypeSite(request.getTypeSite());
        
        site.setQuemSomos(request.getQuemSomos());
        site.setServicos(request.getServicos());
        site.setLogoOpcao(request.getLogoOpcao());
        site.setEmailDesejado(request.getEmailDesejado());
        site.setBannerOpcao(request.getBannerOpcao());
        
        site.setBannerIaDescricao(request.getBannerIaDescricao());
        
        site.setBannerProfissionalDescricao(request.getBannerProfissionalDescricao());
        site.setEmailEmpresa(request.getEmailEmpresa());
        site.setTelefoneEmpresa(request.getTelefoneEmpresa());
        site.setEnderecoEmpresa(request.getEnderecoEmpresa());
        site.setSecao1Titulo(request.getSecao1Titulo());
        site.setSecao1Conteudo(request.getSecao1Conteudo());
        site.setSecao2Titulo(request.getSecao2Titulo());
        site.setSecao2Conteudo(request.getSecao2Conteudo());
        site.setCorPrincipal(request.getCorPrincipal());
        site.setCorSecundaria(request.getCorSecundaria());
        site.setEstilo(request.getEstilo());
        site.setObservacoes(request.getObservacoes());
        site.setFacebook(request.getFacebook());
        site.setLinkedin(request.getLinkedin());
        site.setYoutube(request.getYoutube());
        site.setTiktok(request.getTiktok());
        site.setInstagram(request.getInstagram());
        site.setStatus(0); // Pendente
        
        // Salva o site
        site = siteRepository.save(site);
        
        // Faz upload dos arquivos
        String empresaImagemPath = null;
        String logoPath = null;
        String faviconUrl = null;
        String bannerTextoImgUrl = null;
        
        List<String> servicosImagensPaths = null;
        
        try {
            if (request.getEmpresaImagem() != null && !request.getEmpresaImagem().isEmpty()) {
                String keyPrefix = "erp/sites/" + site.getSiteId() + "/empresa";
                empresaImagemPath = fileUploadService.uploadFile(request.getEmpresaImagem(), keyPrefix);
            }

            if (request.getLogo() != null && !request.getLogo().isEmpty()) {
                String keyPrefix = "erp/sites/" + site.getSiteId() + "/logo";
                logoPath = fileUploadService.uploadFile(request.getLogo(), keyPrefix);
            }
            if (request.getFavicon() != null && !request.getFavicon().isEmpty()) {
                String keyPrefix = "erp/sites/" + site.getSiteId() + "/favicon";
                faviconUrl = fileUploadService.uploadFile(request.getFavicon(), keyPrefix);
                site.setFavicon(faviconUrl);
            }

            if (request.getServicosImagens() != null && !request.getServicosImagens().isEmpty()) {
                String keyPrefix = "erp/sites/" + site.getSiteId() + "/servicos";
                servicosImagensPaths = fileUploadService.uploadFiles(request.getServicosImagens(), keyPrefix);
            }

            // Banners (imagens opcionais)
            if (request.getBannerTextoImg() != null && !request.getBannerTextoImg().isEmpty()) {
                bannerTextoImgUrl = fileUploadService.uploadFile(request.getBannerTextoImg(), "erp/sites/" + site.getSiteId() + "/banners");
                site.setBannerTextoImg(bannerTextoImgUrl);
            }
            
        } catch (IOException e) {
            // Continua sem interromper o cadastro do site (evita rollback da transação)
        }

        // Atualiza o site com os caminhos dos arquivos
        site.setEmpresaImagem(empresaImagemPath);
        site.setLogo(logoPath);
        if (servicosImagensPaths != null && !servicosImagensPaths.isEmpty()) {
            site.setServicosImagens(String.join(",", servicosImagensPaths));
        }
        siteRepository.save(site);

        // Persiste imagens de serviços em tabela dedicada
        if (servicosImagensPaths != null && !servicosImagensPaths.isEmpty()) {
            for (String url : servicosImagensPaths) {
                br.tec.omny.auth.entity.SiteImage img = new br.tec.omny.auth.entity.SiteImage();
                img.setSiteId(site.getSiteId());
                img.setUrl(url);
                siteImageRepository.save(img);
            }
        }

        // Cria o projeto
        Project project = new Project();
        project.setName("Site: " + request.getNomeSite());
        project.setClientId(client.getUserId().intValue());
        project.setBillingType(1); // Por hora
        project.setStartDate(LocalDate.now());
        project.setAddedFrom(1); // ID do staff padrão
        
                // Monta a descrição do projeto
                StringBuilder projectDescription = new StringBuilder();
                projectDescription.append("PROJETO DE CRIAÇÃO DE SITE\n\n");
                projectDescription.append("ORIENTAÇÕES GERAIS:\n");
                projectDescription.append("• Criar um site moderno, atrativo e 100% responsivo utilizando Bootstrap.\n");
                projectDescription.append("• Todas as imagens devem ser carregadas via URL absoluta gerada neste processo (sem referências locais).\n");
                projectDescription.append("• Seguir boas práticas de UX/UI, acessibilidade e performance.\n\n");
        projectDescription.append("INFORMAÇÕES DO CLIENTE:\n");
        projectDescription.append("• Empresa: ").append(client.getCompany()).append("\n");
        projectDescription.append("• Nome do Site: ").append(request.getNomeSite()).append("\n");
        projectDescription.append("• Domínio: ").append(request.getDominio()).append("\n");
        projectDescription.append("• Email: ").append(contact.getEmail()).append("\n");
                projectDescription.append("• Telefone (contato principal): ").append(contact.getPhoneNumber()).append("\n\n");
        
        // Define o contexto baseado no preference
        if ("descricao".equals(request.getPreference())) {
            // Quando preference == "descricao", usa o description_site como contexto
            projectDescription.append("CONTEXTO DO SITE:\n");
            projectDescription.append(request.getDescriptionSite()).append("\n\n");
        } else {
            // Mantém a lógica original
            projectDescription.append("DESCRIÇÃO DO NEGÓCIO:\n");
            projectDescription.append(request.getDescricaoNegocio()).append("\n\n");
            
            projectDescription.append("PÚBLICO ALVO:\n");
            projectDescription.append(request.getPublicoAlvo()).append("\n\n");
        }
        
        projectDescription.append("TIPO DO SITE:\n");
        projectDescription.append("• Tipo: ").append(request.getTipoSite()).append("\n\n");
        
        projectDescription.append("CONTEÚDO DO SITE:\n");
        projectDescription.append("• Banner Principal: ").append(request.getBannerTexto()).append("\n");
        
        projectDescription.append("• Política de Banners: Caso o cliente não envie imagens de banner, gerar automaticamente um banner com base em cor_principal (\"")
            .append(request.getCorPrincipal()).append("\"), cor_secundaria (\"")
            .append(request.getCorSecundaria()).append("\") e estilo (\"")
            .append(request.getEstilo()).append("\"). Seguir identidade visual e boa legibilidade.\n");
        projectDescription.append("• Quem Somos: ").append(request.getQuemSomos()).append("\n");
        projectDescription.append("• Serviços: ").append(request.getServicos()).append("\n");
        projectDescription.append("• Seção 1 - ").append(request.getSecao1Titulo()).append(": ").append(request.getSecao1Conteudo()).append("\n");
        projectDescription.append("• Seção 2 - ").append(request.getSecao2Titulo()).append(": ").append(request.getSecao2Conteudo()).append("\n\n");
        
                projectDescription.append("INFORMAÇÕES DE CONTATO:\n");
        projectDescription.append("• Email da Empresa: ").append(request.getEmailEmpresa()).append("\n");
        projectDescription.append("• Telefone da Empresa: ").append(request.getTelefoneEmpresa()).append("\n");
        projectDescription.append("• Endereço: ").append(request.getEnderecoEmpresa()).append("\n\n");

                // WhatsApp (número sanitizado com código do Brasil 55)
                String whatsappSanitized = (request.getTelefoneEmpresa() == null ? "" : request.getTelefoneEmpresa().replaceAll("\\\\D", ""));
                if (!whatsappSanitized.isEmpty()) {
                    if (!whatsappSanitized.startsWith("55")) {
                        whatsappSanitized = "55" + whatsappSanitized;
                    }
                    projectDescription.append("ATENDIMENTO VIA WHATSAPP:\n");
                    projectDescription.append("• Adicionar ícone flutuante no canto inferior direito\n");
                    projectDescription.append("• Ao clicar, abrir WhatsApp Web em: https://wa.me/").append(whatsappSanitized).append("\n\n");
                }
        
        projectDescription.append("DESIGN:\n");
        projectDescription.append("• Cor Principal: ").append(request.getCorPrincipal()).append("\n");
        projectDescription.append("• Cor Secundária: ").append(request.getCorSecundaria()).append("\n");
        projectDescription.append("• Estilo: ").append(request.getEstilo()).append("\n\n");
        
        if (empresaImagemPath != null) {
            projectDescription.append("• Imagem da Empresa: ").append(empresaImagemPath).append("\n");
        }
        if (logoPath != null) {
            projectDescription.append("• Logo: ").append(logoPath).append("\n");
        }
        if (faviconUrl != null) {
            projectDescription.append("• Favicon: ").append(faviconUrl).append("\n");
        }
        if (bannerTextoImgUrl != null) {
            projectDescription.append("• Banner Principal (imagem): ").append(bannerTextoImgUrl).append("\n");
        }
        
        if (servicosImagensPaths != null && !servicosImagensPaths.isEmpty()) {
            projectDescription.append("• Imagens dos Serviços: ").append(String.join(", ", servicosImagensPaths)).append("\n");
        }
        
        if (request.getObservacoes() != null && !request.getObservacoes().trim().isEmpty()) {
            projectDescription.append("\nOBSERVAÇÕES:\n");
            projectDescription.append(request.getObservacoes());
        }
        
        project.setDescription(projectDescription.toString());
        
        // Salva o projeto
        project = projectRepository.save(project);
        
        // Cria a task principal
        Task task = new Task();
        task.setName("Criação do Site: " + request.getNomeSite());
        task.setRelId(project.getId());
        task.setRelType("project");
        task.setStartDate(LocalDate.now());
        task.setAddedFrom(1); // ID do staff padrão
        task.setStatus(1); // Não iniciado
        task.setBillable(true);
        task.setVisibleToClient(true);
        
        // Monta a descrição detalhada da task
        StringBuilder taskDescription = new StringBuilder();
        taskDescription.append("TAREFA: CRIAÇÃO DE SITE\n\n");
                taskDescription.append("PROMPT PARA CRIAÇÃO DO SITE:\n\n");
                taskDescription.append("INSTRUÇÃO DE ESTRUTURA DE SAÍDA:\n");
                taskDescription.append("• Crie uma pasta com o ID do cliente (userId: ").append(client.getUserId()).append(") e dentro dela crie um arquivo index.php.\n");
                taskDescription.append("• Construa todo o site dentro do arquivo index.php.\n\n");
                taskDescription.append("ORIENTAÇÕES GERAIS:\n");
                taskDescription.append("1. Construir um site moderno, atrativo e responsivo usando Bootstrap (grid, componentes e utilitários).\n");
                taskDescription.append("2. Utilizar apenas as URLs fornecidas para todas as imagens (logo, empresa, serviços e banners).\n");
                taskDescription.append("3. Otimizar para acessibilidade, SEO e performance (lazy loading de imagens quando possível).\n\n");
        taskDescription.append("INSTRUÇÕES INICIAIS:\n");
        taskDescription.append("1. Crie um site de acordo com o tipo especificado\n");
        taskDescription.append("2. Crie uma pasta com o ID: ").append(client.getUserId()).append("\n");
        taskDescription.append("3. Coloque o ID retornado (userId): ").append(client.getUserId()).append("\n");
        taskDescription.append("4. Dentro da pasta, crie um arquivo index.php\n");
        taskDescription.append("5. Escreva no index.php todo o site de acordo com as informações abaixo\n\n");
        
        taskDescription.append("Crie um site moderno e responsivo para a empresa ").append(request.getCompany()).append(" com as seguintes especificações:\n\n");
        
        taskDescription.append("1. INFORMAÇÕES BÁSICAS:\n");
        taskDescription.append("   • Nome do Site: ").append(request.getNomeSite()).append("\n");
        taskDescription.append("   • Domínio: ").append(request.getDominio()).append("\n");
        taskDescription.append("   • Empresa: ").append(request.getCompany()).append("\n");
        taskDescription.append("   • Tipo do Site: ").append(request.getTipoSite()).append("\n");
        taskDescription.append("   • ID da Pasta: ").append(client.getUserId()).append("\n\n");
        
        taskDescription.append("2. CONTEÚDO:\n");
        taskDescription.append("   • Banner Principal: ").append(request.getBannerTexto()).append("\n");
        
        taskDescription.append("   • Caso nenhuma imagem de banner seja fornecida, CRIAR automaticamente um banner com base nas cores especificadas (cor_principal: \"")
            .append(request.getCorPrincipal()).append("\", cor_secundaria: \"")
            .append(request.getCorSecundaria()).append("\") e estilo: \"")
            .append(request.getEstilo()).append("\"). Garantir contraste, tipografia adequada e foco na mensagem.\n");
        taskDescription.append("   • Seção Quem Somos: ").append(request.getQuemSomos()).append("\n");
        taskDescription.append("   • Descrição do Negócio: ").append(request.getDescricaoNegocio()).append("\n");
        taskDescription.append("   • Público Alvo: ").append(request.getPublicoAlvo()).append("\n");
        taskDescription.append("   • Serviços: ").append(request.getServicos()).append("\n");
        taskDescription.append("   • Seção 1 - ").append(request.getSecao1Titulo()).append(": ").append(request.getSecao1Conteudo()).append("\n");
        taskDescription.append("   • Seção 2 - ").append(request.getSecao2Titulo()).append(": ").append(request.getSecao2Conteudo()).append("\n\n");
        
                taskDescription.append("3. CONTATO:\n");
        taskDescription.append("   • Email: ").append(request.getEmailEmpresa()).append("\n");
        taskDescription.append("   • Telefone: ").append(request.getTelefoneEmpresa()).append("\n");
        taskDescription.append("   • Endereço: ").append(request.getEnderecoEmpresa()).append("\n\n");

                // Instrução do botão flutuante do WhatsApp
                String whatsappSanitizedTask = (request.getTelefoneEmpresa() == null ? "" : request.getTelefoneEmpresa().replaceAll("\\\\D", ""));
                if (!whatsappSanitizedTask.isEmpty()) {
                    if (!whatsappSanitizedTask.startsWith("55")) {
                        whatsappSanitizedTask = "55" + whatsappSanitizedTask;
                    }
                    taskDescription.append("3.1. WHATSAPP FLUTUANTE:\n");
                    taskDescription.append("   • Criar ícone do WhatsApp flutuante, fixo no canto inferior direito\n");
                    taskDescription.append("   • Ao clicar, abrir https://wa.me/").append(whatsappSanitizedTask).append(" (WhatsApp Web) em nova aba\n");
                    taskDescription.append("   • Garantir contraste adequado e não obstruir conteúdo\n\n");
                }
        
        taskDescription.append("4. DESIGN:\n");
        taskDescription.append("   • Cor Principal: ").append(request.getCorPrincipal()).append("\n");
        taskDescription.append("   • Cor Secundária: ").append(request.getCorSecundaria()).append("\n");
        taskDescription.append("   • Estilo: ").append(request.getEstilo()).append("\n\n");
        
        taskDescription.append("5. ARQUIVOS FORNECIDOS:\n");
        if (empresaImagemPath != null) {
            taskDescription.append("   • Imagem da Empresa: ").append(empresaImagemPath).append("\n");
        }
        if (logoPath != null) {
            taskDescription.append("   • Logo: ").append(logoPath).append("\n");
        }
        if (faviconUrl != null) {
            taskDescription.append("   • Favicon: ").append(faviconUrl).append("\n");
        }
        if (bannerTextoImgUrl != null) {
            taskDescription.append("   • Banner Principal (imagem): ").append(bannerTextoImgUrl).append("\n");
        }
        
        if (servicosImagensPaths != null && !servicosImagensPaths.isEmpty()) {
            taskDescription.append("   • Imagens dos Serviços: ").append(String.join(", ", servicosImagensPaths)).append("\n");
        }
        taskDescription.append("\n");
        
        taskDescription.append("6. INSTRUÇÕES ESPECÍFICAS POR TIPO:\n");
        String tipoSite = request.getTipoSite().toLowerCase();
        if (tipoSite.equals("site")) {
            taskDescription.append("   • Criar um site institucional completo em PHP\n");
            taskDescription.append("   • Incluir todas as seções solicitadas no index.php\n");
            taskDescription.append("   • Focar em apresentação da empresa e serviços\n");
            taskDescription.append("   • Usar HTML, CSS e PHP no arquivo index.php\n");
        } else if (tipoSite.equals("curriculo")) {
            taskDescription.append("   • Criar um site de currículo profissional em PHP\n");
            taskDescription.append("   • Focar em apresentação pessoal e experiência\n");
            taskDescription.append("   • Incluir seção de habilidades e formação\n");
            taskDescription.append("   • Design limpo e profissional no index.php\n");
        } else if (tipoSite.equals("cartao de visita")) {
            taskDescription.append("   • Criar um site tipo cartão de visita digital em PHP\n");
            taskDescription.append("   • Design minimalista e direto\n");
            taskDescription.append("   • Focar em informações de contato\n");
            taskDescription.append("   • Página única com navegação suave no index.php\n");
        }
        taskDescription.append("\n");
        
        taskDescription.append("7. REQUISITOS TÉCNICOS:\n");
        taskDescription.append("   • Site responsivo (mobile-first)\n");
        taskDescription.append("   • Otimizado para SEO\n");
        taskDescription.append("   • Carregamento rápido\n");
        taskDescription.append("   • Compatível com todos os navegadores modernos\n");
        taskDescription.append("   • Formulário de contato funcional\n");
        taskDescription.append("   • Integração com redes sociais (se aplicável)\n\n");
        
        if (request.getObservacoes() != null && !request.getObservacoes().trim().isEmpty()) {
            taskDescription.append("8. OBSERVAÇÕES ESPECIAIS:\n");
            taskDescription.append(request.getObservacoes()).append("\n\n");
        }
        
        taskDescription.append("INSTRUÇÕES PARA O DESENVOLVEDOR:\n");
        taskDescription.append("1. Analise todas as informações fornecidas\n");
        taskDescription.append("2. Crie um design moderno e atrativo seguindo as cores especificadas\n");
        taskDescription.append("3. Implemente todas as seções solicitadas\n");
        taskDescription.append("4. Garanta que o site seja totalmente responsivo\n");
        taskDescription.append("5. Teste em diferentes dispositivos e navegadores\n");
        taskDescription.append("6. Otimize para performance e SEO\n");
        taskDescription.append("7. Entre em contato com o cliente para feedback e ajustes\n");
        
        task.setDescription(taskDescription.toString());
        
        // Salva a task principal
        taskRepository.save(task);
        
        // Cria a Task 2 - Configurações do Site (4 itens)
        Task taskConfig = new Task();
        taskConfig.setName("Configurações do Site: " + request.getNomeSite());
        taskConfig.setRelId(project.getId());
        taskConfig.setRelType("project");
        taskConfig.setStartDate(LocalDate.now());
        taskConfig.setAddedFrom(1); // ID do staff padrão
        taskConfig.setStatus(1); // Não iniciado
        taskConfig.setBillable(true);
        taskConfig.setVisibleToClient(true);
        
        StringBuilder taskConfigDescription = new StringBuilder();
        taskConfigDescription.append("TAREFA: CONFIGURAÇÕES DO SITE\n\n");
        taskConfigDescription.append("Esta task contém 4 itens essenciais para a configuração do site:\n\n");
        
        taskConfigDescription.append("1. CRIAR DOMÍNIO\n");
        taskConfigDescription.append("   • Domínio solicitado: ").append(request.getDominio()).append("\n");
        taskConfigDescription.append("   • Configurar DNS\n");
        taskConfigDescription.append("   • Verificar disponibilidade\n");
        taskConfigDescription.append("   • Registrar domínio se necessário\n\n");
        
        taskConfigDescription.append("2. GERAR HTTPS\n");
        taskConfigDescription.append("   • Instalar certificado SSL\n");
        taskConfigDescription.append("   • Configurar redirecionamento HTTP para HTTPS\n");
        taskConfigDescription.append("   • Testar certificado\n");
        taskConfigDescription.append("   • Verificar segurança do site\n\n");
        
        taskConfigDescription.append("3. GERAR EMAIL\n");
        taskConfigDescription.append("   • Email da empresa: ").append(request.getEmailEmpresa()).append("\n");
        taskConfigDescription.append("   • Configurar contas de email\n");
        taskConfigDescription.append("   • Configurar servidor de email\n");
        taskConfigDescription.append("   • Testar envio e recebimento\n\n");
        
        taskConfigDescription.append("4. ENTRAR EM CONTATO COM CLIENTE\n");
        taskConfigDescription.append("   • Cliente: ").append(request.getFirstname()).append(" ").append(request.getLastname()).append("\n");
        taskConfigDescription.append("   • Email: ").append(request.getEmail()).append("\n");
        taskConfigDescription.append("   • Telefone: ").append(request.getPhonenumber()).append("\n");
        taskConfigDescription.append("   • Confirmar configurações\n");
        taskConfigDescription.append("   • Agendar reunião de validação\n");
        taskConfigDescription.append("   • Enviar credenciais de acesso\n\n");
        
        taskConfigDescription.append("INSTRUÇÕES:\n");
        taskConfigDescription.append("1. Execute cada item sequencialmente\n");
        taskConfigDescription.append("2. Documente todas as configurações realizadas\n");
        taskConfigDescription.append("3. Teste cada funcionalidade antes de marcar como concluída\n");
        taskConfigDescription.append("4. Mantenha o cliente informado sobre o progresso\n");
        
        taskConfig.setDescription(taskConfigDescription.toString());
        taskRepository.save(taskConfig);
        
        // Cria a Task 3 - Geração de Fatura
        Task taskFatura = new Task();
        taskFatura.setName("Geração de Fatura: " + request.getNomeSite());
        taskFatura.setRelId(project.getId());
        taskFatura.setRelType("project");
        taskFatura.setStartDate(LocalDate.now());
        taskFatura.setAddedFrom(1); // ID do staff padrão
        taskFatura.setStatus(1); // Não iniciado
        taskFatura.setBillable(true);
        taskFatura.setVisibleToClient(false); // Não visível para o cliente até ser aprovada
        
        StringBuilder taskFaturaDescription = new StringBuilder();
        taskFaturaDescription.append("TAREFA: GERAÇÃO DE FATURA\n\n");
        taskFaturaDescription.append("Esta task será executada após a validação final do cliente.\n\n");
        
        taskFaturaDescription.append("INFORMAÇÕES DO CLIENTE:\n");
        taskFaturaDescription.append("• Empresa: ").append(request.getCompany()).append("\n");
        taskFaturaDescription.append("• Nome: ").append(request.getFirstname()).append(" ").append(request.getLastname()).append("\n");
        taskFaturaDescription.append("• Email: ").append(request.getEmail()).append("\n");
        taskFaturaDescription.append("• Telefone: ").append(request.getPhonenumber()).append("\n");
        taskFaturaDescription.append("• Site: ").append(request.getNomeSite()).append("\n");
        taskFaturaDescription.append("• Domínio: ").append(request.getDominio()).append("\n\n");
        
        taskFaturaDescription.append("SERVIÇOS INCLUÍDOS:\n");
        taskFaturaDescription.append("• Criação e desenvolvimento do site\n");
        taskFaturaDescription.append("• Design responsivo e moderno\n");
        taskFaturaDescription.append("• Configuração de domínio\n");
        taskFaturaDescription.append("• Certificado SSL (HTTPS)\n");
        taskFaturaDescription.append("• Configuração de email corporativo\n");
        taskFaturaDescription.append("• Otimização para SEO\n");
        taskFaturaDescription.append("• Suporte e manutenção inicial\n\n");
        
        taskFaturaDescription.append("INSTRUÇÕES PARA GERAÇÃO DA FATURA:\n");
        taskFaturaDescription.append("1. Aguardar aprovação final do cliente\n");
        taskFaturaDescription.append("2. Calcular valor total dos serviços\n");
        taskFaturaDescription.append("3. Gerar fatura no sistema\n");
        taskFaturaDescription.append("4. Enviar fatura para o cliente\n");
        taskFaturaDescription.append("5. Acompanhar pagamento\n");
        taskFaturaDescription.append("6. Marcar projeto como concluído após pagamento\n\n");
        
        taskFaturaDescription.append("OBSERVAÇÕES:\n");
        if (request.getObservacoes() != null && !request.getObservacoes().trim().isEmpty()) {
            taskFaturaDescription.append("• ").append(request.getObservacoes()).append("\n");
        }
        taskFaturaDescription.append("• Verificar se todos os serviços foram entregues conforme especificado\n");
        taskFaturaDescription.append("• Confirmar satisfação do cliente antes de gerar fatura\n");
        
        taskFatura.setDescription(taskFaturaDescription.toString());
        taskRepository.save(taskFatura);
        
        // Salva o contexto final no campo description_site do site (independente da jornada)
        site.setDescriptionSite(projectDescription.toString());
        siteRepository.save(site);
        
        // Envia mensagem para fila MQ com site_id, client_id, contact_id e contexto exatamente igual ao da Task principal
        queueService.sendSiteCreationMessage(site.getSiteId(), client.getUserId().intValue(), contact.getId(), taskDescription.toString());
        
        return client;
    }
    
    /**
     * Busca informações básicas de um cliente por ID
     * @param clientId ID do cliente
     * @return Informações básicas do cliente (company, email, phoneNumber)
     * @throws Exception Se o cliente não for encontrado
     */
    public ClientInfoResponse getClientInfo(Long clientId) throws Exception {
        // Busca o cliente
        Optional<Client> clientOpt = clientRepository.findById(clientId);
        
        if (clientOpt.isEmpty()) {
            throw new Exception("Cliente não encontrado");
        }
        
        Client client = clientOpt.get();
        
        // Busca o contato primário do cliente
        Optional<Contact> contactOpt = contactRepository.findByUserIdAndIsPrimary(clientId, true);
        
        String email = null;
        if (contactOpt.isPresent()) {
            email = contactOpt.get().getEmail();
        }
        
        return new ClientInfoResponse(
            client.getCompany(),
            email,
            client.getPhoneNumber()
        );
    }
    
    /**
     * Conta quantos sites um cliente possui
     * @param clientId ID do cliente
     * @return Número de sites do cliente
     * @throws Exception Se o cliente não for encontrado
     */
    public long getClientSitesCount(Long clientId) throws Exception {
        // Verifica se o cliente existe
        if (!clientRepository.existsById(clientId)) {
            throw new Exception("Cliente não encontrado");
        }
        
        return siteRepository.countByClientId(clientId.intValue());
    }
    
    /**
     * Agenda o envio de email para ser executado APÓS o commit da transação
     * @param contactId ID do contato
     */
    private void scheduleEmailAfterCommit(Long contactId) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    // Aguarda 2 segundos para garantir que o banco foi atualizado
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    sendWelcomeEmailAsync(contactId);
                }
            });
        } else {
            // Se não há transação ativa, envia imediatamente
            sendWelcomeEmailAsync(contactId);
        }
    }

    /**
     * Envia email de boas-vindas de forma assíncrona
     * @param contactId ID do contato
     */
    @Async
    public CompletableFuture<Void> sendWelcomeEmailAsync(Long contactId) {
        try {
            emailService.sendWelcomeEmail(contactId);
        } catch (Exception e) {
            // Log do erro mas não interrompe o cadastro
            System.err.println("Erro ao enviar email de boas-vindas para contactId " + contactId + ": " + e.getMessage());
        }
        
        return CompletableFuture.completedFuture(null);
    }
}

