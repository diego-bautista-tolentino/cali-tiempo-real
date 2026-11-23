package estructura.de.datos.practicas;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Supplier;

/**
 *
 * @author Diego
 */
public class calificacionesEnTiempoReal {

    public static void main(String[] args) {
        SistemaCalificaciones sistema = new SistemaCalificaciones();
        Random random = new Random();

        //casos limite: consultas con la lista vacia
        System.out.println("=== Lista vacia ===");
        probar("Minima", sistema::obtenerMinima);
        probar("Maxima", sistema::obtenerMaxima);
        probar("Mediana", sistema::obtenerMediana);

        //casos limite: calificaciones fuera de rango
        System.out.println("\n=== Fuera de rango ===");
        System.out.print("Agregar -1.5: ");
        sistema.agregarCalificacion(-1.5);
        System.out.print("Agregar 11.0: ");
        sistema.agregarCalificacion(11.0);

        //simulacion en tiempo real con calificaciones aleatorias (N impar = 5)
        System.out.println("\n=== Simulacion en tiempo real ===");
        for (int i = 0; i < 5; i++) {
            double valor = random.nextDouble() * 10;
            double calificacion = Math.round(valor * 10) / 10.0; //redondeo a 1 decimal
            sistema.agregarCalificacion(calificacion);
            System.out.print("Llega " + calificacion + " -> ");
            sistema.mostrarCalificaciones();
        }
        probar("Mediana (N impar)", sistema::obtenerMediana);

        //calificacion repetida para probar duplicados (N par = 6)
        System.out.println("\n=== Calificacion repetida ===");
        double repetida = sistema.obtenerEnPosicion(2);
        sistema.agregarCalificacion(repetida);
        System.out.print("Llega " + repetida + " (repetida) -> ");
        sistema.mostrarCalificaciones();
        probar("Mediana (N par)", sistema::obtenerMediana);

        //consultas generales y posiciones validas e invalidas
        System.out.println("\n=== Consultas ===");
        System.out.println("Total de entregas: " + sistema.totalEntregas());
        probar("Minima", sistema::obtenerMinima);
        probar("Maxima", sistema::obtenerMaxima);
        probar("Posicion 1", () -> sistema.obtenerEnPosicion(1));
        probar("Posicion 100", () -> sistema.obtenerEnPosicion(100));

        //busqueda de una calificacion existente y una inexistente
        System.out.println("\n=== Busqueda ===");
        System.out.println("Existe " + repetida + "? " + sistema.existeCalificacion(repetida));
        System.out.println("Existe 10.5? " + sistema.existeCalificacion(10.5));

        //correccion con nota nueva valida
        System.out.println("\n=== Correccion ===");
        double anterior = sistema.obtenerEnPosicion(0);
        System.out.println("Corregir " + anterior + " por 9.5: "
                + sistema.corregirCalificacion(anterior, 9.5));
        sistema.mostrarCalificaciones();

        //correccion con nota nueva invalida (la lista no debe cambiar)
        anterior = sistema.obtenerEnPosicion(0);
        System.out.println("Corregir " + anterior + " por 12.0: "
                + sistema.corregirCalificacion(anterior, 12.0));
        sistema.mostrarCalificaciones();

        //eliminacion de una calificacion existente y una inexistente
        System.out.println("\n=== Eliminacion ===");
        System.out.println("Eliminar " + repetida + ": " + sistema.eliminarCalificacion(repetida));
        sistema.mostrarCalificaciones();
        System.out.println("Eliminar 10.5 (no existe): " + sistema.eliminarCalificacion(10.5));
        sistema.mostrarCalificaciones();
    }

    //ejecuta una consulta y muestra el resultado o el error si ocurre una excepcion
    private static void probar(String nombre, Supplier<Double> consulta) {
        try {
            System.out.println(nombre + ": " + consulta.get());
        } catch (Exception e) {
            System.out.println(nombre + ": error -> " + e.getMessage());
        }
    }
}

class SistemaCalificaciones {

    //lista de calificaciones siempre ordenada de menor a mayor
    private ArrayList<Double> lista;

    public SistemaCalificaciones() {
        lista = new ArrayList<>();
    }

    //inserta la calificacion en su posicion ordenada usando busqueda binaria
    public void agregarCalificacion(double calificacion) {
        if (calificacion < 0 || calificacion > 10) {
            System.out.println("Calificacion invalida.");
            return;
        }
        int inicio = 0;
        int fin = lista.size();
        while (inicio < fin) {
            int medio = inicio + (fin - inicio) / 2;
            if (lista.get(medio) < calificacion) {
                inicio = medio + 1;
            } else {
                fin = medio;
            }
        }
        lista.add(inicio, calificacion);
    }

    //devuelve la calificacion mas baja o -1 si la lista esta vacia
    public double obtenerMinima() {
        if (lista.isEmpty()) {
            return -1;
        }
        return lista.get(0);
    }

    //devuelve la calificacion mas alta o -1 si la lista esta vacia
    public double obtenerMaxima() {
        if (lista.isEmpty()) {
            return -1;
        }
        return lista.get(lista.size() - 1);
    }

    //devuelve la mediana o -1 si la lista esta vacia
    public double obtenerMediana() {
        int n = lista.size();
        if (n == 0) {
            return -1;
        }
        if (n % 2 == 1) {
            return lista.get(n / 2);
        }
        return (lista.get(n / 2 - 1) + lista.get(n / 2)) / 2.0;
    }

    //devuelve la calificacion en la posicion indicada o -1 si la posicion es invalida
    public double obtenerEnPosicion(int posicion) {
        if (posicion < 0 || posicion >= lista.size()) {
            return -1;
        }
        return lista.get(posicion);
    }

    //devuelve el numero de calificaciones registradas
    public int totalEntregas() {
        return lista.size();
    }

    //imprime la lista completa de calificaciones
    public void mostrarCalificaciones() {
        System.out.println(lista);
    }

    //busqueda binaria: devuelve el indice de la calificacion o -1 si no existe
    private int buscarIndice(double calificacion) {
        int inicio = 0;
        int fin = lista.size() - 1;
        while (inicio <= fin) {
            int medio = inicio + (fin - inicio) / 2; //evita desbordamiento
            int comparacion = Double.compare(lista.get(medio), calificacion);
            if (comparacion == 0) {
                return medio;
            } else if (comparacion < 0) {
                inicio = medio + 1;
            } else {
                fin = medio - 1;
            }
        }
        return -1;
    }

    //indica si la calificacion existe en la lista
    public boolean existeCalificacion(double calificacion) {
        return buscarIndice(calificacion) != -1;
    }

    //elimina la calificacion si existe
    public boolean eliminarCalificacion(double calificacion) {
        int indice = buscarIndice(calificacion);
        if (indice == -1) {
            return false;
        }
        lista.remove(indice); //remove(int): elimina por posicion
        return true;
    }

    //corrige una calificacion manteniendo la lista ordenada
    public boolean corregirCalificacion(double anterior, double nueva) {
        //se valida antes de eliminar para no perder la calificacion original
        if (nueva < 0 || nueva > 10) {
            return false;
        }
        if (!eliminarCalificacion(anterior)) {
            return false;
        }
        agregarCalificacion(nueva);
        return true;
    }
}