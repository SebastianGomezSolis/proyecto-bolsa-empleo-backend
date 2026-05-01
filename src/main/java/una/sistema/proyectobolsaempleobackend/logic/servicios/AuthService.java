package una.sistema.proyectobolsaempleobackend.logic.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import una.sistema.proyectobolsaempleobackend.data.AdministradorRepository;
import una.sistema.proyectobolsaempleobackend.data.EmpresaRepository;
import una.sistema.proyectobolsaempleobackend.data.OferenteRepository;
import una.sistema.proyectobolsaempleobackend.dto.LoginRequest;
import una.sistema.proyectobolsaempleobackend.logic.model.*;

@Service
public class AuthService {

    @Autowired private UsuarioService usuarioService;
    @Autowired private PasswordHash passwordHash;
    @Autowired private SesionUsuarioBean sesionUsuarioBean;
    @Autowired private AdministradorRepository administradorRepository;
    @Autowired private EmpresaRepository empresaRepository;
    @Autowired private OferenteRepository oferenteRepository;

    /**
     * Autentica al usuario y carga la sesión con su rol y referenciaId al perfil.
     * @return null si OK, mensaje de error si falla
     */
    public String login(LoginRequest request) {
        if (request.getCorreo() == null || request.getClave() == null) {
            return "Credenciales inválidas";
        }

        Usuario usuario = usuarioService.findByCorreo(request.getCorreo());
        if (usuario == null || !Boolean.TRUE.equals(usuario.getActivo())) {
            return "Credenciales inválidas";
        }

        if (!passwordHash.verify(request.getClave(), usuario.getClave())) {
            return "Credenciales inválidas";
        }

        // Resolver referenciaId según el rol
        Integer referenciaId = resolverReferenciaId(usuario);

        sesionUsuarioBean.login(usuario.getId(), usuario.getCorreo(), usuario.getRol(), referenciaId);
        return null;
    }

    public void logout() {
        sesionUsuarioBean.logout();
    }

    private Integer resolverReferenciaId(Usuario usuario) {
        return switch (usuario.getRol()) {
            case ADMIN -> administradorRepository.findByUsuario(usuario)
                    .map(Administrador::getId).orElse(null);
            case EMPRESA -> empresaRepository.findByUsuario(usuario)
                    .map(Empresa::getId).orElse(null);
            case OFERENTE -> oferenteRepository.findByUsuario(usuario)
                    .map(Oferente::getId).orElse(null);
        };
    }
}
