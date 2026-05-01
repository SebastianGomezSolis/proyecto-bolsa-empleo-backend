package una.sistema.proyectobolsaempleobackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import una.sistema.proyectobolsaempleobackend.dto.LoginRequest;
import una.sistema.proyectobolsaempleobackend.dto.RegistroEmpresaRequest;
import una.sistema.proyectobolsaempleobackend.dto.RegistroOferenteRequest;
import una.sistema.proyectobolsaempleobackend.logic.ModeloDatos;
import una.sistema.proyectobolsaempleobackend.logic.model.SesionUsuarioBean;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private ModeloDatos modeloDatos;

    @Autowired
    private SesionUsuarioBean sesionUsuarioBean;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String error = modeloDatos.getAuthService().login(request);
        if (error != null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        return ResponseEntity.ok(Map.of(
            "id", sesionUsuarioBean.getId(),
            "correo", sesionUsuarioBean.getCorreo(),
            "rol", sesionUsuarioBean.getRol().name(),
            "referenciaId", sesionUsuarioBean.getReferenciaId()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        modeloDatos.getAuthService().logout();
        return ResponseEntity.ok("Sesión cerrada");
    }

    @PostMapping("/registro/empresa")
    public ResponseEntity<?> registrarEmpresa(@RequestBody RegistroEmpresaRequest req) {
        String error = modeloDatos.getEmpresaService().registrar(
            req.getCorreo(), req.getClave(), req.getNombre(),
            req.getLocalizacion(), req.getTelefono(), req.getDescripcion()
        );
        if (error != null) return ResponseEntity.badRequest().body(error);
        return ResponseEntity.ok("Registro exitoso. Espere la aprobación del administrador.");
    }

    @PostMapping("/registro/oferente")
    public ResponseEntity<?> registrarOferente(@RequestBody RegistroOferenteRequest req) {
        String error = modeloDatos.getOferenteService().registrar(
            req.getCorreo(), req.getClave(), req.getIdentificacion(),
            req.getNombre(), req.getPrimerApellido(), req.getIsoNacionalidad(),
            req.getTelefono(), req.getLugarResidencia()
        );
        if (error != null) return ResponseEntity.badRequest().body(error);
        return ResponseEntity.ok("Registro exitoso. Espere la aprobación del administrador.");
    }

    @GetMapping("/sesion")
    public ResponseEntity<?> sesion() {
        if (!sesionUsuarioBean.isLogueado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No hay sesión activa");
        }
        return ResponseEntity.ok(Map.of(
            "id", sesionUsuarioBean.getId(),
            "correo", sesionUsuarioBean.getCorreo(),
            "rol", sesionUsuarioBean.getRol().name(),
            "referenciaId", sesionUsuarioBean.getReferenciaId()
        ));
    }
}
