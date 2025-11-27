package br.tec.omny.auth.service;

import br.tec.omny.auth.dto.AdminLoginRequest;
import br.tec.omny.auth.dto.AdminLoginResponse;
import br.tec.omny.auth.dto.ClientInfoResponse;
import br.tec.omny.auth.dto.LoginRequest;
import br.tec.omny.auth.dto.RegisterRequest;
import br.tec.omny.auth.dto.SiteRegisterRequest;
import br.tec.omny.auth.entity.Client;
import br.tec.omny.auth.entity.Contact;
import br.tec.omny.auth.entity.ContactPermission;
import br.tec.omny.auth.entity.Project;
import br.tec.omny.auth.entity.Referral;
import br.tec.omny.auth.entity.Site;
import br.tec.omny.auth.entity.Staff;
import br.tec.omny.auth.entity.Task;
import br.tec.omny.auth.entity.Warehouse;
import br.tec.omny.auth.repository.AffiliateRepository;
import br.tec.omny.auth.repository.ReferralRepository;
import br.tec.omny.auth.repository.ClientRepository;
import br.tec.omny.auth.repository.ContactPermissionRepository;
import br.tec.omny.auth.repository.ContactRepository;
import br.tec.omny.auth.repository.ProjectRepository;
import br.tec.omny.auth.repository.SiteRepository;
import br.tec.omny.auth.repository.StaffRepository;
import br.tec.omny.auth.repository.TaskRepository;
import br.tec.omny.auth.repository.WarehouseRepository;
import br.tec.omny.auth.repository.SiteImageRepository;
import br.tec.omny.auth.util.JwtUtil;
import br.tec.omny.auth.util.PasswordHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.web.multipart.MultipartFile;
import java.util.concurrent.CompletableFuture;

import java.time.LocalDate;
import java.util.ArrayList;
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
    private ContactPermissionRepository contactPermissionRepository;

    @Autowired
    private AffiliateRepository affiliateRepository;
    
    @Autowired
    private ReferralRepository referralRepository;

    @Autowired
    private FileUploadService fileUploadService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private RecaptchaService recaptchaService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Value("${app.site.max-sites-per-client:3}")
    private int maxSitesPerClient;
    
    private final PasswordHash passwordHash = new PasswordHash(8, false);

    private String buildCompany(String company, String firstName, String lastName) {
        if (company != null && !company.trim().isEmpty()) {
            return company.trim();
        }
        if (firstName != null && !firstName.trim().isEmpty() && 
            lastName != null && !lastName.trim().isEmpty()) {
            return firstName.trim() + " " + lastName.trim();
        }
        return "Nova Empresa";
    }
    
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
        boolean newClientCreated = false;
        
        // Validações baseadas no preference
        if ("descricao".equals(request.getPreference())) {
            // Se preference == "descricao", description_site é obrigatório
            if (request.getDescriptionSite() == null || request.getDescriptionSite().trim().isEmpty()) {
                throw new Exception("Descrição do site é obrigatória quando preference é 'descricao'");
            }
        }
        
        // Verifica se já existe um contato registrado com o email informado
        Optional<Contact> existingEmailContact = Optional.empty();
        String normalizedEmail = request.getEmail() != null ? request.getEmail().trim() : null;
        if (request.getUserId() == null && normalizedEmail != null && !normalizedEmail.isEmpty()) {
            existingEmailContact = contactRepository.findByEmail(normalizedEmail);
        }

        // Validação de password obrigatório apenas quando não existe user_id e não estamos usando um contato já existente
        if (request.getUserId() == null && existingEmailContact.isEmpty()) {
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
                newClientCreated = true;
                
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
            if (existingEmailContact.isPresent()) {
                contact = existingEmailContact.get();
                client = clientRepository.findById(contact.getUserId())
                    .orElseThrow(() -> new Exception("Cliente associado ao email não foi encontrado"));

                String normalizedFirstName = request.getFirstname() != null ? request.getFirstname().trim() : null;
                String normalizedLastName = request.getLastname() != null ? request.getLastname().trim() : null;
                String normalizedPhone = request.getPhonenumber() != null ? request.getPhonenumber().trim() : null;
                String companyCandidate = buildCompany(request.getCompany(), normalizedFirstName, normalizedLastName);

                if (!"Nova Empresa".equals(companyCandidate) || 
                    client.getCompany() == null || client.getCompany().trim().isEmpty()) {
                    client.setCompany(companyCandidate);
                }
                if (normalizedPhone != null && !normalizedPhone.isEmpty()) {
                    client.setPhoneNumber(normalizedPhone);
                    contact.setPhoneNumber(normalizedPhone);
                }
                if (normalizedFirstName != null && !normalizedFirstName.isEmpty()) {
                    contact.setFirstName(normalizedFirstName);
                }
                if (normalizedLastName != null && !normalizedLastName.isEmpty()) {
                    contact.setLastName(normalizedLastName);
                }
                if (normalizedEmail != null && !normalizedEmail.isEmpty()) {
                    contact.setEmail(normalizedEmail);
                }

                client = clientRepository.save(client);
                contact = contactRepository.save(contact);
            } else {
                // Cria o cliente
                client = new Client();
                client.setCompany(buildCompany(request.getCompany(), request.getFirstname(), request.getLastname()));
                client.setPhoneNumber(request.getPhonenumber());
                client.setActive(true);
                client.setDefaultClient(true);

                // Salva o cliente
                client = clientRepository.save(client);
                newClientCreated = true;
                
                // Cria o contato
                contact = new Contact();
                contact.setUserId(client.getUserId());
                contact.setFirstName(request.getFirstname());
                contact.setLastName(request.getLastname());
                contact.setEmail(normalizedEmail);
                contact.setPhoneNumber(request.getPhonenumber());
                contact.setIsPrimary(true);
                contact.setPassword(passwordHash.hashPassword(request.getPassword())); // Senha fornecida pelo usuário
                
                // Salva o contato
                contact = contactRepository.save(contact);
                assignDefaultContactPermissions(contact);
                
                // Verifica se o cadastro foi salvo com sucesso antes de enviar email
                if (contact.getId() != null) {
                    // Agenda o email para ser enviado APÓS o commit da transação
                    scheduleEmailAfterCommit(contact.getId());
                }
            }
        }
        
        maybeCreateReferral(request, client, newClientCreated);

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
        site.setProductId(request.getProductId());
        site.setAfm(request.getAfm());
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
        String empresaImagemPath = uploadFileSafe(request.getEmpresaImagem(), "erp/sites/" + site.getSiteId() + "/empresa");
        String logoPath = uploadFileSafe(request.getLogo(), "erp/sites/" + site.getSiteId() + "/logo");
        String faviconUrl = uploadFileSafe(request.getFavicon(), "erp/sites/" + site.getSiteId() + "/favicon");
        String bannerTextoImgUrl = uploadFileSafe(request.getBannerTextoImg(), "erp/sites/" + site.getSiteId() + "/banners");

        if (faviconUrl != null) {
            site.setFavicon(faviconUrl);
        }
        if (bannerTextoImgUrl != null) {
            site.setBannerTextoImg(bannerTextoImgUrl);
        }

        List<String> servicosImagensPaths = uploadFilesSafe(request.getServicosImagens(), "erp/sites/" + site.getSiteId() + "/servicos");

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
        project.setName("Projeto: " + request.getNomeSite() + "("+site.getSiteId()+")");
        project.setClientId(client.getUserId().intValue());
        project.setBillingType(1); // Por hora
        project.setStartDate(LocalDate.now());
        project.setStatus(1);
        project.setAddedFrom(1); // ID do staff padrão
        
                // Monta a descrição do projeto
                StringBuilder projectDescription = new StringBuilder();
                projectDescription.append("PROJETO DE CRIAÇÃO DE SITE E SISTEMAS<br><br>");
                projectDescription.append("ORIENTAÇÕES GERAIS:<br>");
                projectDescription.append("• Criar um site ou sistema moderno, atrativo e 100% responsivo utilizando Bootstrap.<br>");
                projectDescription.append("• Todas as imagens devem ser carregadas via URL absoluta gerada neste processo (sem referências locais).<br>");
                projectDescription.append("• Seguir boas práticas de UX/UI, acessibilidade e performance.<br><br>");
        projectDescription.append("INFORMAÇÕES DO CLIENTE:<br>");
        projectDescription.append("• Empresa: ").append(client.getCompany()).append("<br>");
        projectDescription.append("• Nome do Site: ").append(request.getNomeSite()).append("<br>");
        projectDescription.append("• Domínio: ").append(request.getDominio()).append("<br>");
        projectDescription.append("• Email: ").append(contact.getEmail()).append("<br>");
                projectDescription.append("• Telefone (contato principal): ").append(contact.getPhoneNumber()).append("<br><br>");
        
        // Define o contexto baseado no preference
        if ("descricao".equals(request.getPreference())) {
            // Quando preference == "descricao", usa o description_site como contexto
            projectDescription.append("CONTEXTO DO SITE:<br>");
            projectDescription.append(request.getDescriptionSite()).append("<br><br>");
        } else {
            // Mantém a lógica original
            projectDescription.append("DESCRIÇÃO DO NEGÓCIO:<br>");
            projectDescription.append(request.getDescricaoNegocio()).append("<br><br>");
            
            projectDescription.append("PÚBLICO ALVO:<br>");
            projectDescription.append(request.getPublicoAlvo()).append("<br><br>");
        }
        

        projectDescription.append("CONTEÚDO:<br>");
        projectDescription.append("• Banner Principal: ").append(request.getBannerTexto()).append("<br>");
        
        projectDescription.append("• Política de Banners: Caso o cliente não envie imagens de banner, gerar automaticamente um banner com base em cor_principal (\"")
            .append(request.getCorPrincipal()).append("\"), cor_secundaria (\"")
            .append(request.getCorSecundaria()).append("\") e estilo (\"")
            .append(request.getEstilo()).append("\"). Seguir identidade visual e boa legibilidade.<br>");
        projectDescription.append("• Quem Somos: ").append(request.getQuemSomos()).append("<br>");
        projectDescription.append("• Serviços: ").append(request.getServicos()).append("<br>");
        projectDescription.append("• Seção 1 - ").append(request.getSecao1Titulo()).append(": ").append(request.getSecao1Conteudo()).append("<br>");
        projectDescription.append("• Seção 2 - ").append(request.getSecao2Titulo()).append(": ").append(request.getSecao2Conteudo()).append("<br><br>");
        
                projectDescription.append("INFORMAÇÕES DE CONTATO:<br>");
        projectDescription.append("• Email da Empresa: ").append(request.getEmailEmpresa()).append("<br>");
        projectDescription.append("• Telefone da Empresa: ").append(request.getTelefoneEmpresa()).append("<br>");
        projectDescription.append("• Endereço: ").append(request.getEnderecoEmpresa()).append("<br><br>");

                // WhatsApp (número sanitizado com código do Brasil 55)
                String whatsappSanitized = (request.getTelefoneEmpresa() == null ? "" : request.getTelefoneEmpresa().replaceAll("\\\\D", ""));
                if (!whatsappSanitized.isEmpty()) {
                    if (!whatsappSanitized.startsWith("55")) {
                        whatsappSanitized = "55" + whatsappSanitized;
                    }
                    projectDescription.append("ATENDIMENTO VIA WHATSAPP:<br>");
                    projectDescription.append("• Adicionar ícone flutuante no canto inferior direito<br>");
                    projectDescription.append("• Ao clicar, abrir WhatsApp Web em: https://wa.me/").append(whatsappSanitized).append("<br><br>");
                }
        
        projectDescription.append("DESIGN:<br>");
        projectDescription.append("• Cor Principal: ").append(request.getCorPrincipal()).append("<br>");
        projectDescription.append("• Cor Secundária: ").append(request.getCorSecundaria()).append("<br>");
        projectDescription.append("• Estilo: ").append(request.getEstilo()).append("<br><br>");
        
        if (empresaImagemPath != null) {
            projectDescription.append("• Imagem da Empresa: ").append(empresaImagemPath).append("<br>");
        }
        if (logoPath != null) {
            projectDescription.append("• Logo: ").append(logoPath).append("<br>");
        }
        if (faviconUrl != null) {
            projectDescription.append("• Favicon: ").append(faviconUrl).append("<br>");
        }
        if (bannerTextoImgUrl != null) {
            projectDescription.append("• Banner Principal (imagem): ").append(bannerTextoImgUrl).append("<br>");
        }
        
        if (servicosImagensPaths != null && !servicosImagensPaths.isEmpty()) {
            projectDescription.append("• Imagens dos Serviços: ").append(String.join(", ", servicosImagensPaths)).append("<br>");
        }
        
        if (request.getObservacoes() != null && !request.getObservacoes().trim().isEmpty()) {
            projectDescription.append("<br>OBSERVAÇÕES:<br>");
            projectDescription.append(request.getObservacoes());
        }
        
        project.setDescription(projectDescription.toString());
        
        // Salva o projeto
        project = projectRepository.save(project);
        
        // Cria a task principal
        Task task = new Task();
        task.setName("Criação do Site");
        task.setRelId(project.getId());
        task.setRelType("project");
        task.setStartDate(LocalDate.now());
        task.setAddedFrom(1); // ID do staff padrão
        task.setStatus(1); // Não iniciado
        task.setBillable(true);
        task.setVisibleToClient(true);
        
        // Monta a descrição detalhada da task
        StringBuilder taskDescription = new StringBuilder();
        taskDescription.append("• Produto ID: ").append(request.getProductId()).append("<br><br>");
        taskDescription.append("• Site ID: ").append(site.getSiteId()).append("<br><br>");

        taskDescription.append("TAREFA: CRIAÇÃO DE SITE OU SISTEMA<br><br>");
                taskDescription.append("PROMPT PARA CRIAÇÃO:<br><br>");
                taskDescription.append("INSTRUÇÃO DE ESTRUTURA DE SAÍDA:<br>");
                taskDescription.append("• Crie uma pasta com o ID do cliente (userId: ").append(client.getUserId()).append(") e dentro dela crie um arquivo index.php.<br>");
                taskDescription.append("• Construa todo o site dentro do arquivo index.php.<br><br>");
                taskDescription.append("ORIENTAÇÕES GERAIS:<br>");
                taskDescription.append("1. Construir um site moderno, atrativo e responsivo usando Bootstrap (grid, componentes e utilitários).<br>");
                taskDescription.append("2. Utilizar apenas as URLs fornecidas para todas as imagens (logo, empresa, serviços e banners).<br>");
                taskDescription.append("3. Otimizar para acessibilidade, SEO e performance (lazy loading de imagens quando possível).<br><br>");
        taskDescription.append("INSTRUÇÕES INICIAIS:<br>");
        taskDescription.append("1. Crie um site de acordo com o tipo especificado<br>");
        taskDescription.append("2. Crie uma pasta com o ID: ").append(client.getUserId()).append("<br>");
        taskDescription.append("3. Coloque o ID retornado (userId): ").append(client.getUserId()).append("<br>");
        taskDescription.append("4. Dentro da pasta, crie um arquivo index.php<br>");
        taskDescription.append("5. Escreva no index.php todo o site de acordo com as informações abaixo<br><br>");
        
        taskDescription.append("Crie um site moderno e responsivo para a empresa ").append(request.getCompany()).append(" com as seguintes especificações:<br><br>");
        
        taskDescription.append("1. INFORMAÇÕES BÁSICAS:<br>");
        taskDescription.append("   • Nome do Site: ").append(request.getNomeSite()).append("<br>");
        taskDescription.append("   • Domínio: ").append(request.getDominio()).append("<br>");
        taskDescription.append("   • Empresa: ").append(request.getCompany()).append("<br>");
        taskDescription.append("   • Tipo do Site: ").append(request.getTipoSite()).append("<br>");
        taskDescription.append("   • ID da Pasta: ").append(client.getUserId()).append("<br><br>");
        
        taskDescription.append("2. CONTEÚDO:<br>");
        taskDescription.append("   • Banner Principal: ").append(request.getBannerTexto()).append("<br>");
        
        taskDescription.append("   • Caso nenhuma imagem de banner seja fornecida, CRIAR automaticamente um banner com base nas cores especificadas (cor_principal: \"")
            .append(request.getCorPrincipal()).append("\", cor_secundaria: \"")
            .append(request.getCorSecundaria()).append("\") e estilo: \"")
            .append(request.getEstilo()).append("\"). Garantir contraste, tipografia adequada e foco na mensagem.<br>");
        taskDescription.append("   • Seção Quem Somos: ").append(request.getQuemSomos()).append("<br>");
        taskDescription.append("   • Descrição do Negócio: ").append(request.getDescricaoNegocio()).append("<br>");
        taskDescription.append("   • Público Alvo: ").append(request.getPublicoAlvo()).append("<br>");
        taskDescription.append("   • Serviços: ").append(request.getServicos()).append("<br>");
        taskDescription.append("   • Seção 1 - ").append(request.getSecao1Titulo()).append(": ").append(request.getSecao1Conteudo()).append("<br>");
        taskDescription.append("   • Seção 2 - ").append(request.getSecao2Titulo()).append(": ").append(request.getSecao2Conteudo()).append("<br><br>");
        
                taskDescription.append("3. CONTATO:<br>");
        taskDescription.append("   • Email: ").append(request.getEmailEmpresa()).append("<br>");
        taskDescription.append("   • Telefone: ").append(request.getTelefoneEmpresa()).append("<br>");
        taskDescription.append("   • Endereço: ").append(request.getEnderecoEmpresa()).append("<br><br>");

                // Instrução do botão flutuante do WhatsApp
                String whatsappSanitizedTask = (request.getTelefoneEmpresa() == null ? "" : request.getTelefoneEmpresa().replaceAll("\\\\D", ""));
                if (!whatsappSanitizedTask.isEmpty()) {
                    if (!whatsappSanitizedTask.startsWith("55")) {
                        whatsappSanitizedTask = "55" + whatsappSanitizedTask;
                    }
                    taskDescription.append("3.1. WHATSAPP FLUTUANTE:<br>");
                    taskDescription.append("   • Criar ícone do WhatsApp flutuante, fixo no canto inferior direito<br>");
                    taskDescription.append("   • Ao clicar, abrir https://wa.me/").append(whatsappSanitizedTask).append(" (WhatsApp Web) em nova aba<br>");
                    taskDescription.append("   • Garantir contraste adequado e não obstruir conteúdo<br><br>");
                }
        
        taskDescription.append("4. DESIGN:<br>");
        taskDescription.append("   • Cor Principal: ").append(request.getCorPrincipal()).append("<br>");
        taskDescription.append("   • Cor Secundária: ").append(request.getCorSecundaria()).append("<br>");
        taskDescription.append("   • Estilo: ").append(request.getEstilo()).append("<br><br>");
        
        taskDescription.append("5. ARQUIVOS FORNECIDOS:<br>");
        if (empresaImagemPath != null) {
            taskDescription.append("   • Imagem da Empresa: ").append(empresaImagemPath).append("<br>");
        }
        if (logoPath != null) {
            taskDescription.append("   • Logo: ").append(logoPath).append("<br>");
        }
        if (faviconUrl != null) {
            taskDescription.append("   • Favicon: ").append(faviconUrl).append("<br>");
        }
        if (bannerTextoImgUrl != null) {
            taskDescription.append("   • Banner Principal (imagem): ").append(bannerTextoImgUrl).append("<br>");
        }
        
        if (servicosImagensPaths != null && !servicosImagensPaths.isEmpty()) {
            taskDescription.append("   • Imagens dos Serviços: ").append(String.join(", ", servicosImagensPaths)).append("<br>");
        }
        taskDescription.append("<br>");
        
        taskDescription.append("6. INSTRUÇÕES ESPECÍFICAS POR TIPO:<br>");
        String tipoSite = request.getTipoSite().toLowerCase();
        if (tipoSite.equals("site")) {
            taskDescription.append("   • Criar um site institucional completo em PHP<br>");
            taskDescription.append("   • Incluir todas as seções solicitadas no index.php<br>");
            taskDescription.append("   • Focar em apresentação da empresa e serviços<br>");
            taskDescription.append("   • Usar HTML, CSS e PHP no arquivo index.php<br>");
        } else if (tipoSite.equals("curriculo")) {
            taskDescription.append("   • Criar um site de currículo profissional em PHP<br>");
            taskDescription.append("   • Focar em apresentação pessoal e experiência<br>");
            taskDescription.append("   • Incluir seção de habilidades e formação<br>");
            taskDescription.append("   • Design limpo e profissional no index.php<br>");
        } else if (tipoSite.equals("cartao de visita")) {
            taskDescription.append("   • Criar um site tipo cartão de visita digital em PHP<br>");
            taskDescription.append("   • Design minimalista e direto<br>");
            taskDescription.append("   • Focar em informações de contato<br>");
            taskDescription.append("   • Página única com navegação suave no index.php<br>");
        }
        taskDescription.append("<br>");
        
        taskDescription.append("7. REQUISITOS TÉCNICOS:<br>");
        taskDescription.append("   • Site responsivo (mobile-first)<br>");
        taskDescription.append("   • Otimizado para SEO<br>");
        taskDescription.append("   • Carregamento rápido<br>");
        taskDescription.append("   • Compatível com todos os navegadores modernos<br>");
        taskDescription.append("   • Formulário de contato funcional<br>");
        taskDescription.append("   • Integração com redes sociais (se aplicável)<br><br>");
        
        if (request.getObservacoes() != null && !request.getObservacoes().trim().isEmpty()) {
            taskDescription.append("8. OBSERVAÇÕES ESPECIAIS:<br>");
            taskDescription.append(request.getObservacoes()).append("<br><br>");
        }
        
        taskDescription.append("INSTRUÇÕES PARA O DESENVOLVEDOR:<br>");
        taskDescription.append("1. Analise todas as informações fornecidas<br>");
        taskDescription.append("2. Crie um design moderno e atrativo seguindo as cores especificadas<br>");
        taskDescription.append("3. Implemente todas as seções solicitadas<br>");
        taskDescription.append("4. Garanta que o site ou sistema seja totalmente responsivo<br>");
        taskDescription.append("5. Teste em diferentes dispositivos e navegadores<br>");
        taskDescription.append("6. Otimize para performance e SEO<br>");
        taskDescription.append("7. Entre em contato com o cliente para feedback e ajustes<br>");
        
        task.setDescription(taskDescription.toString());
        
        // Salva a task principal
        taskRepository.save(task);
        
        // Cria a Task 2 - Configurações do Site (4 itens)
        Task taskConfig = new Task();
        taskConfig.setName("Configurações do Sistema: " + site.getSiteId());
        taskConfig.setRelId(project.getId());
        taskConfig.setRelType("project");
        taskConfig.setStartDate(LocalDate.now());
        taskConfig.setAddedFrom(1); // ID do staff padrão
        taskConfig.setStatus(1); // Não iniciado
        taskConfig.setBillable(true);
        taskConfig.setVisibleToClient(true);
        
        StringBuilder taskConfigDescription = new StringBuilder();
        taskConfigDescription.append("TAREFA: CONFIGURAÇÕES DO SITE<br><br>");
        taskConfigDescription.append("Esta task contém 4 itens essenciais para a configuração do site:<br><br>");
        
        taskConfigDescription.append("1. CRIAR DOMÍNIO<br>");
        taskConfigDescription.append("   • Domínio solicitado: ").append(request.getDominio()).append("<br>");
        taskConfigDescription.append("   • Configurar DNS<br>");
        taskConfigDescription.append("   • Verificar disponibilidade<br>");
        taskConfigDescription.append("   • Registrar domínio se necessário<br>");
        
        taskConfigDescription.append("2. GERAR HTTPS<br>");
        taskConfigDescription.append("   • Instalar certificado SSL<br>");
        taskConfigDescription.append("   • Configurar redirecionamento HTTP para HTTPS<br>");
        taskConfigDescription.append("   • Testar certificado<br>");
        taskConfigDescription.append("   • Verificar segurança do site<br><br>");
        
        taskConfigDescription.append("3. GERAR EMAIL<br>");
        taskConfigDescription.append("   • Email da empresa: ").append(request.getEmailEmpresa()).append("<br>");
        taskConfigDescription.append("   • Configurar contas de email<br>");
        taskConfigDescription.append("   • Configurar servidor de email<br>");
        taskConfigDescription.append("   • Testar envio e recebimento<br><br>");
        
        taskConfigDescription.append("4. ENTRAR EM CONTATO COM CLIENTE<br>");
        taskConfigDescription.append("   • Cliente: ").append(request.getFirstname()).append(" ").append(request.getLastname()).append("<br>");
        taskConfigDescription.append("   • Email: ").append(request.getEmail()).append("<br>");
        taskConfigDescription.append("   • Telefone: ").append(request.getPhonenumber()).append("<br>");
        taskConfigDescription.append("   • Confirmar configurações<br>");
        taskConfigDescription.append("   • Agendar reunião de validação<br>");
        taskConfigDescription.append("   • Enviar credenciais de acesso<br><br>");
        
        taskConfigDescription.append("INSTRUÇÕES:<br>");
        taskConfigDescription.append("1. Execute cada item sequencialmente<br>");
        taskConfigDescription.append("2. Documente todas as configurações realizadas<br>");
        taskConfigDescription.append("3. Teste cada funcionalidade antes de marcar como concluída<br>");
        taskConfigDescription.append("4. Mantenha o cliente informado sobre o progresso<br>");
        
        taskConfig.setDescription(taskConfigDescription.toString());
        taskRepository.save(taskConfig);
        
        // Cria a Task 3 - Geração de Fatura
        Task taskFatura = new Task();
        taskFatura.setName("Geração de Fatura");
        taskFatura.setRelId(project.getId());
        taskFatura.setRelType("project");
        taskFatura.setStartDate(LocalDate.now());
        taskFatura.setAddedFrom(1); // ID do staff padrão
        taskFatura.setStatus(1); // Não iniciado
        taskFatura.setBillable(true);
        taskFatura.setVisibleToClient(false); // Não visível para o cliente até ser aprovada
        
        StringBuilder taskFaturaDescription = new StringBuilder();
        taskFaturaDescription.append("TAREFA: GERAÇÃO DE FATURA<br>");
        taskFaturaDescription.append("Esta task será executada após a validação final do cliente.<br><br>");
        
        taskFaturaDescription.append("INFORMAÇÕES DO CLIENTE:<br>");
        taskFaturaDescription.append("• Site ID: ").append(request.getDominio()).append("<br><br>");
        taskFaturaDescription.append("• Produto ID: ").append(request.getProductId()).append("<br><br>");
        taskFaturaDescription.append("• Empresa: ").append(request.getCompany()).append("<br>");
        taskFaturaDescription.append("• Nome: ").append(request.getFirstname()).append(" ").append(request.getLastname()).append("<br>");
        taskFaturaDescription.append("• Email: ").append(request.getEmail()).append("<br>");
        taskFaturaDescription.append("• Telefone: ").append(request.getPhonenumber()).append("<br>");
        taskFaturaDescription.append("• Site: ").append(request.getNomeSite()).append("<br>");
        taskFaturaDescription.append("• Domínio: ").append(request.getDominio()).append("<br>");

        

        taskFaturaDescription.append("INSTRUÇÕES PARA GERAÇÃO DA FATURA:<br>");
        taskFaturaDescription.append("1. Aguardar aprovação final do cliente<br>");
        taskFaturaDescription.append("2. Calcular valor total dos serviços<br>");
        taskFaturaDescription.append("3. Gerar fatura no sistema<br>");
        taskFaturaDescription.append("4. Enviar fatura para o cliente<br>");
        taskFaturaDescription.append("5. Acompanhar pagamento<br>");
        taskFaturaDescription.append("6. Marcar projeto como concluído após pagamento<br><br>");
        
        taskFaturaDescription.append("OBSERVAÇÕES:<br>");
        if (request.getObservacoes() != null && !request.getObservacoes().trim().isEmpty()) {
            taskFaturaDescription.append("• ").append(request.getObservacoes()).append("<br>");
        }
        taskFaturaDescription.append("• Verificar se todos os serviços foram entregues conforme especificado<br>");
        taskFaturaDescription.append("• Confirmar satisfação do cliente antes de gerar fatura<br>");
        
        taskFatura.setDescription(taskFaturaDescription.toString());
        taskRepository.save(taskFatura);
        
        // Salva o contexto final no campo description_site do site (independente da jornada)
        site.setDescriptionSite(projectDescription.toString());
        siteRepository.save(site);
        
        // Envia mensagem para fila MQ com site_id, client_id, contact_id e contexto exatamente igual ao da Task principal
       /*
        Integer clientIdInteger = null;
        if (client.getUserId() != null) {
            clientIdInteger = Integer.valueOf(client.getUserId().intValue());
        }
        queueService.sendSiteCreationMessage(site.getSiteId(), clientIdInteger, contact.getId(), taskDescription.toString(), request.getIa());
        */
        return client;
    }

    private void maybeCreateReferral(SiteRegisterRequest request, Client client, boolean newClientCreated) {
        if (!newClientCreated) {
            return;
        }
        String afmSlug = request.getAfm();
        if (afmSlug == null || afmSlug.trim().isEmpty()) {
            return;
        }

        affiliateRepository.findByAffiliateSlug(afmSlug.trim()).ifPresent(affiliate -> {
            Long userId = client.getUserId();
            if (userId == null) {
                return;
            }
            Referral referral = new Referral();
            referral.setAffiliateId(affiliate.getAffiliateId());
            referral.setClientId(userId.intValue());
            referral.setUa("");
            referral.setIp("127.0.0.1");
            referralRepository.save(referral);
        });
    }

    private void assignDefaultContactPermissions(Contact contact) {
        if (contact == null || contact.getId() == null) {
            return;
        }

        int[] permissions = {1, 3, 5, 6};
        for (int permission : permissions) {
            ContactPermission entity = new ContactPermission();
            entity.setPermissionId(permission);
            entity.setUserid(contact.getId());
            contactPermissionRepository.save(entity);
        }
    }

    
    
    /**
     * Busca informações básicas de um cliente por ID
     * @param clientId ID do cliente
     * @return Informações básicas do cliente (company, email, phoneNumber)
     * @throws Exception Se o cliente não for encontrado
     */
    private String uploadFileSafe(MultipartFile file, String keyPrefix) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            return fileUploadService.uploadFile(file, keyPrefix);
        } catch (Exception ignored) {
            return null;
        }
    }

    private List<String> uploadFilesSafe(List<MultipartFile> files, String keyPrefix) {
        List<String> urls = new ArrayList<>();
        if (files == null || files.isEmpty()) {
            return urls;
        }

        for (MultipartFile file : files) {
            String url = uploadFileSafe(file, keyPrefix);
            if (url != null) {
                urls.add(url);
            }
        }

        return urls;
    }

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

