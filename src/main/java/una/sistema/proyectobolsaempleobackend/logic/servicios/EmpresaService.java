package una.sistema.proyectobolsaempleobackend.logic.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import una.sistema.proyectobolsaempleobackend.data.EmpresaRepository;
import una.sistema.proyectobolsaempleobackend.logic.model.Empresa;
import una.sistema.proyectobolsaempleobackend.logic.model.Rol;
import una.sistema.proyectobolsaempleobackend.logic.model.Usuario;

import java.util.List;

@Service
public class EmpresaService {

    @Autowired private EmpresaRepository empresaRepository;
    @Autowired private UsuarioService usuarioService;

    public Iterable<Empresa> findAll() {
        return empresaRepository.findAll();
    }

    public Empresa findById(Integer id) {
        return empresaRepository.findById(id).orElse(null);
    }

    public List<Empresa> findPendientes() {
        return empresaRepository.findByAutorizadoFalse();
    }

    /**
     * Registra empresa: crea usuario con rol EMPRESA y luego el perfil.
     * @return null si OK, mensaje de error si falla
     */
    public String registrar(String correo, String clave, String nombre, String localizacion,
                            String telefono, String descripcion) {
        if (correo == null || correo.isBlank()) return "El correo es requerido";
        if (clave == null || clave.isBlank())   return "La clave es requerida";
        if (nombre == null || nombre.isBlank()) return "El nombre es requerido";

        if (usuarioService.existeCorreo(correo)) return "El correo ya está registrado";

        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setClave(clave);
        usuario.setRol(Rol.EMPRESA);
        usuario.setActivo(false); // inactivo hasta que admin autorice
        Usuario guardado = usuarioService.guardar(usuario);

        Empresa empresa = new Empresa();
        empresa.setUsuario(guardado);
        empresa.setNombre(nombre);
        empresa.setLocalizacion(localizacion);
        empresa.setTelefono(telefono);
        empresa.setDescripcion(descripcion);
        empresa.setAutorizado(false);
        empresaRepository.save(empresa);
        return null;
    }

    public String autorizar(Integer id) {
        Empresa empresa = findById(id);
        if (empresa == null) return "Empresa no encontrada";
        empresa.setAutorizado(true);
        empresaRepository.save(empresa);
        Usuario usuario = empresa.getUsuario();
        usuario.setActivo(true);
        usuarioService.guardarSinHash(usuario);
        return null;
    }
}
