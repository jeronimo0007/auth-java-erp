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
     * @param contexto Contexto/descrição do site
     */
    public void sendSiteCreationMessage(Integer siteId, String contexto) {
        try {
            SiteCreationMessage message = new SiteCreationMessage(siteId, contexto);
            
            rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
            
            logger.info("Mensagem enviada para fila de criação de site - Site ID: {}, Exchange: {}, Routing Key: {}", 
                       siteId, exchangeName, routingKey);
                       
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem para fila de criação de site - Site ID: {}", siteId, e);
            // Não interrompe o fluxo principal em caso de erro na fila
        }
    }
}
