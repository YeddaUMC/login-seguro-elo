package com.pfc.thindesk.config;

import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.repository.ChamadoRepository;
import java.time.LocalDateTime;
import java.util.Random;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoDataConfig {
    @Bean
    CommandLineRunner dadosFicticios(ChamadoRepository repository,
            @Value("${thindesk.demo.seed:false}") boolean habilitado) {
        return args -> {
            if (!habilitado || repository.count() > 0) return;
            String[] tipos = {"Acesso ao sistema", "Rede", "Equipamento", "Impressora", "Software", "E-mail"};
            String[] tecnicos = {"Ana Costa", "Bruno Lima", "Carla Martins"};
            String[] status = {"ABERTO", "EM_ANDAMENTO", "CONCLUIDO", "CONCLUIDO", "CONCLUIDO", "CANCELADO"};
            Random random = new Random(42);
            LocalDateTime agora = LocalDateTime.now();
            for (int i = 0; i < 72; i++) {
                Chamado c = new Chamado();
                c.setDescricao("Atendimento de " + tipos[i % tipos.length].toLowerCase() + " - ocorrência " + (i + 1));
                c.setTipo(tipos[i % tipos.length]);
                c.setTecnico(tecnicos[i % tecnicos.length]);
                c.setUsuario("Usuário fictício " + (i % 16 + 1));
                c.setPrioridade(i % 11 == 0 ? "URGENTE" : (i % 4 == 0 ? "ALTA" : "NORMAL"));
                c.setStatus(status[(i + random.nextInt(3)) % status.length]);
                c.setDataAbertura(agora.minusDays(random.nextInt(165) + 3).minusHours(random.nextInt(18)));
                c.setDataAtualizacao(agora.minusDays(random.nextInt(25)));
                if ("CONCLUIDO".equals(c.getStatus())) {
                    c.setDataConclusao(c.getDataAbertura().plusHours(random.nextInt(56) + 2));
                    c.setConsideracoesTecnicas("Problema identificado e atendimento concluído após os procedimentos técnicos.");
                } else if ("EM_ANDAMENTO".equals(c.getStatus())) {
                    c.setConsideracoesTecnicas("Análise técnica em andamento.");
                }
                repository.save(c);
            }
        };
    }
}
