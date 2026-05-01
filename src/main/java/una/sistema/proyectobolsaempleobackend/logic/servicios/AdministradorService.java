package una.sistema.proyectobolsaempleobackend.logic.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import una.sistema.proyectobolsaempleobackend.data.AdministradorRepository;
import una.sistema.proyectobolsaempleobackend.logic.model.Administrador;
import una.sistema.proyectobolsaempleobackend.logic.model.Rol;
import una.sistema.proyectobolsaempleobackend.logic.model.Usuario;

@Service
public class AdministradorService {
    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private UsuarioService usuarioService;

    public Iterable<Administrador> findAll() {
        return administradorRepository.findAll();
    }

    public Administrador findById(Integer id) {
        return administradorRepository.findById(id).orElse(null);
    }

    public String crear(String correo, String clave, String identificacion, String nombre) {
        if (correo == null || correo.isBlank())
            return "El correo es requerido";

        if (clave == null || clave.isBlank())
            return "La clave es requerida";

        if (identificacion == null || identificacion.isBlank())
            return "La identificación es requerida";

        if (nombre == null || nombre.isBlank())
            return "El nombre es requerido";

        if (usuarioService.existeCorreo(correo))
            return "El correo ya está registrado";

        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setClave(clave);
        usuario.setRol(Rol.ADMIN);
        usuario.setActivo(true);
        Usuario guardado = usuarioService.guardar(usuario);

        Administrador admin = new Administrador();
        admin.setUsuario(guardado);
        admin.setIdentificacion(identificacion);
        admin.setNombre(nombre);
        administradorRepository.save(admin);
        return null;
    }
}
