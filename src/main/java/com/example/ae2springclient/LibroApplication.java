package com.example.ae2springclient;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Scanner;

@Component
public class LibroApplication implements CommandLineRunner {
    private final RestTemplate restTemplate;
    private final Scanner scanner;

    public LibroApplication(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run(String... args) {
        boolean continuar = true;

        while (continuar) {
            System.out.println("Seleccione una opción:");
            System.out.println("1. Dar de alta un libro");
            System.out.println("2. Dar de baja un libro por ID");
            System.out.println("3. Modificar un libro por ID");
            System.out.println("4. Obtener un libro por ID");
            System.out.println("5. Listar todos los libros");
            System.out.println("6. Salir");

            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir la nueva línea después de leer un entero

            switch (opcion) {
                case 1:
                    darDeAltaLibro();
                    break;
                case 2:
                    darDeBajaLibro();
                    break;
                case 3:
                    modificarLibro();
                    break;
                case 4:
                    obtenerLibroPorId();
                    break;
                case 5:
                    listarTodosLosLibros();
                    break;
                case 6:
                    continuar = false;
                    break;
                default:
                    System.out.println("Opción no válida. Inténtelo de nuevo.");
                    break;
            }
        }
    }

    /**
     * Método para dar de alta un libro. Solicita al usuario los datos del
     * nuevo libro y realiza una solicitud POST al servidor REST.
     */
    private void darDeAltaLibro() {
        // Solicitar datos al usuario
        System.out.print("Ingrese el título del libro: ");
        String titulo = scanner.nextLine();
        System.out.print("Ingrese la editorial del libro: ");
        String editorial = scanner.nextLine();
        System.out.print("Ingrese la nota del libro: ");
        double nota = scanner.nextDouble();

        // Crear objeto Libro con los datos ingresados
        Libro nuevoLibro = new Libro();
        nuevoLibro.setTitulo(titulo);
        nuevoLibro.setEditorial(editorial);
        nuevoLibro.setNota(nota);

        // Enviar la solicitud POST al servidor REST para dar de alta el libro
        restTemplate.postForObject("http://localhost:8080/libros", nuevoLibro, Libro.class);

        System.out.println("Libro dado de alta con éxito.");
    }

    /**
     * Método para dar de baja un libro por ID. Solicita al usuario el
     * ID del libro a dar de baja y realiza una solicitud DELETE al servidor REST.
     */
    private void darDeBajaLibro() {
        System.out.print("Ingrese el ID del libro a dar de baja: ");
        Long id = scanner.nextLong();

        // Enviar la solicitud DELETE al servidor REST para dar de baja el libro
        restTemplate.delete("http://localhost:8080/libros/" + id);

        System.out.println("Libro dado de baja con éxito.");
    }

    /**
     * Método para modificar un libro por ID. Solicita al usuario el
     * ID del libro a modificar, muestra los detalles actuales,
     * solicita los nuevos datos y realiza una solicitud PUT al servidor REST.
     */
    private void modificarLibro() {
        System.out.print("Ingrese el ID del libro a modificar: ");
        Long id = scanner.nextLong();
        scanner.nextLine(); // Consumir la nueva línea después de leer un entero

        // Obtener el libro actual del servidor para mostrar los detalles al usuario
        Libro libroActual = restTemplate.getForObject("http://localhost:8080/libros/" + id, Libro.class);

        if (libroActual != null) {
            System.out.println("Detalles actuales del libro:");
            System.out.println(libroActual.getTitulo());

            // Solicitar los nuevos datos al usuario
            System.out.print("Ingrese el nuevo título del libro: ");
            String nuevoTitulo = scanner.nextLine();
            System.out.print("Ingrese la nueva editorial del libro: ");
            String nuevaEditorial = scanner.nextLine();
            System.out.print("Ingrese la nueva nota del libro: ");
            double nuevaNota = scanner.nextDouble();

            // Actualizar el libro con los nuevos datos
            libroActual.setTitulo(nuevoTitulo);
            libroActual.setEditorial(nuevaEditorial);
            libroActual.setNota(nuevaNota);

            // Enviar la solicitud PUT al servidor REST para modificar el libro
            restTemplate.put("http://localhost:8080/libros/" + id, libroActual, Libro.class);

            System.out.println("Libro modificado con éxito.");
        } else {
            System.out.println("No se encontró un libro con el ID proporcionado.");
        }
    }

    /**
     * Método para obtener un libro por ID. Solicita al usuario
     * el ID del libro a obtener y realiza una solicitud GET al servidor REST.
     */
    private void obtenerLibroPorId() {
        System.out.print("Ingrese el ID del libro a obtener: ");
        Long id = scanner.nextLong();

        // Enviar la solicitud GET al servidor REST para obtener el libro por ID
        Libro libro = restTemplate.getForObject("http://localhost:8080/libros/" + id, Libro.class);

        if (libro != null) {
            System.out.println("Detalles del libro:");
            System.out.println(libro.getTitulo());
        } else {
            System.out.println("No se encontró un libro con el ID proporcionado.");
        }
    }

    /**
     * Método para listar todos los libros. Realiza una solicitud GET
     * al servidor REST para obtener todos los libros y muestra solo los títulos.
     */
    private void listarTodosLosLibros() {
        // Enviar la solicitud GET al servidor REST para obtener todos los libros
        Libro[] libros = restTemplate.getForObject("http://localhost:8080/libros", Libro[].class);

        if (libros != null && libros.length > 0) {
            System.out.println("Lista de libros:");
            Arrays.stream(libros).map(Libro::getTitulo).forEach(System.out::println);
        } else {
            System.out.println("No hay libros registrados.");
        }
    }
}
