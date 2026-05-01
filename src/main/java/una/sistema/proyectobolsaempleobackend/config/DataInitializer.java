package una.sistema.proyectobolsaempleobackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import una.sistema.proyectobolsaempleobackend.data.UsuarioRepository;
import una.sistema.proyectobolsaempleobackend.logic.servicios.AdministradorService;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AdministradorService administradorService;

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByCorreo("admin@sistema.com")) {
            administradorService.crear("admin@sistema.com", "admin123", "ADM-001", "Administrador");
            System.out.println("Admin creado: admin@sistema.com / admin123");
        }
    }
}
