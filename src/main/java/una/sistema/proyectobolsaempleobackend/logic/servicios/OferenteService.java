package una.sistema.proyectobolsaempleobackend.logic.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import una.sistema.proyectobolsaempleobackend.data.NacionalidadRepository;
import una.sistema.proyectobolsaempleobackend.data.OferenteRepository;
import una.sistema.proyectobolsaempleobackend.logic.model.*;

import java.util.List;

@Service
public class OferenteService {

    @Autowired private OferenteRepository oferenteRepository;
    @Autowired private UsuarioService usuarioService;
    @Autowired private NacionalidadRepository nacionalidadRepository;

    public Iterable<Oferente> findAll() {
        return oferenteRepository.findAll();
    }

    public Oferente findById(Integer id) {
        return oferenteRepository.findById(id).orElse(null);
    }

    public List<Oferente> findPendientes() {
        return oferenteRepository.findByAutorizadoFalse();
    }

    public String registrar(String correo, String clave, String identificacion,
                            String nombre, String primerApellido, String isoNacionalidad,
                            String telefono, String lugarResidencia) {
        if (correo == null || correo.isBlank())               return "El correo es requerido";
        if (clave == null || clave.isBlank())                 return "La clave es requerida";
        if (identificacion == null || identificacion.isBlank()) return "La identificación es requerida";
        if (nombre == null || nombre.isBlank())               return "El nombre es requerido";
        if (primerApellido == null || primerApellido.isBlank()) return "El primer apellido es requerido";

        if (usuarioService.existeCorreo(correo))              return "El correo ya está registrado";
        if (oferenteRepository.existsByIdentificacion(identificacion)) return "La identificación ya está registrada";

        Nacionalidad nac = nacionalidadRepository.findById(isoNacionalidad).orElse(null);
        if (nac == null) return "Nacionalidad inválida";

        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setClave(clave);
        usuario.setRol(Rol.OFERENTE);
        usuario.setActivo(false);
        Usuario guardado = usuarioService.guardar(usuario);

        Oferente oferente = new Oferente();
        oferente.setUsuario(guardado);
        oferente.setIdentificacion(identificacion);
        oferente.setNombre(nombre);
        oferente.setPrimerApellido(primerApellido);
        oferente.setNacionalidad(nac);
        oferente.setTelefono(telefono);
        oferente.setLugarResidencia(lugarResidencia);
        oferente.setAutorizado(false);
        oferenteRepository.save(oferente);
        return null;
    }

    public String autorizar(Integer id) {
        Oferente oferente = findById(id);
        if (oferente == null) return "Oferente no encontrado";
        oferente.setAutorizado(true);
        oferenteRepository.save(oferente);
        Usuario usuario = oferente.getUsuario();
        usuario.setActivo(true);
        usuarioService.guardarSinHash(usuario);
        return null;
    }

    public void actualizarCurriculum(Integer id, String ruta) {
        Oferente oferente = findById(id);
        if (oferente != null) {
            oferente.setCurriculum(ruta);
            oferenteRepository.save(oferente);
        }
    }
}
