package una.sistema.proyectobolsaempleobackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import una.sistema.proyectobolsaempleobackend.logic.ModeloDatos;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PuestosController {
    @Autowired private ModeloDatos modeloDatos;

    @GetMapping("/puestos/buscar")
    public ResponseEntity<?> buscarPuestos(@RequestParam(required = false) List<Integer> caracteristicaIds) {
        if (caracteristicaIds == null || caracteristicaIds.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "raices", modeloDatos.getCaracteristicaService().findRaices(),
                    "caracteristicaIds", caracteristicaIds,
                    "tipoCambio", obtenerTipoCambio(),
                    "puestos", List.of()
            ));
        } else {
            List<?> puestos = modeloDatos.getPuestoService().findPublicosActivos().stream()
                    .filter(p -> modeloDatos.getPuestoCaracteristicaService().findByPuesto(p.getId()).stream()
                            .anyMatch(pc -> caracteristicaIds.contains(pc.getCaracteristica().getId())))
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "raices", modeloDatos.getCaracteristicaService().findRaices(),
                    "caracteristicaIds", caracteristicaIds,
                    "tipoCambio", obtenerTipoCambio(),
                    "puestos", puestos
            ));
        }
    }


    private Object obtenerTipoCambio() {
        try {
            return modeloDatos.getTipoCambioServicio().obtenerTipoCambio();
        } catch (Exception e) {
            return null;
        }
    }
}
