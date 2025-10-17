package br.tec.omny.auth.service;

import br.tec.omny.auth.dto.SiteCreationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class QueueService {
    
    private static final Logger logger = LoggerFactory.getLogger(QueueService.class);
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Value("${mq.exchange.site-creation}")
    private String exchangeName;
    
    @Value("${mq.routing-key.site-creation}")
    private String routingKey;
    
    /**
     * Envia mensagem para a fila de criação de site
     * @param siteId ID do site criado
     * @param clientId ID do cliente
     * @param contactId ID do contato
     * @param contexto Contexto/descrição do site (texto plano)
     */
    public void sendSiteCreationMessage(Integer siteId, Integer clientId, Long contactId, String contexto) {
        try {
            // Envia como String (texto plano) para facilitar consumo
            String payload = contexto; // apenas o contexto no corpo
            rabbitTemplate.convertAndSend(exchangeName, routingKey, payload, message -> {
                message.getMessageProperties().setHeader("site_id", siteId);
                if (clientId != null) {
                    message.getMessageProperties().setHeader("client_id", clientId);
                }
                if (contactId != null) {
                    message.getMessageProperties().setHeader("contact_id", contactId);
                }
                return message;
            });
            
            logger.info("Mensagem (String) enviada para fila de criação de site - Site ID: {}, Client ID: {}, Contact ID: {}, Exchange: {}, Routing Key: {}", 
                       siteId, clientId, contactId, exchangeName, routingKey);
                       
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem para fila de criação de site - Site ID: {}", siteId, e);
            // Não interrompe o fluxo principal em caso de erro na fila
        }
    }
}
