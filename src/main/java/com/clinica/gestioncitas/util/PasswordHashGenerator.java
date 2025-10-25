package com.clinica.gestioncitas.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Generador de contraseñas BCrypt para actualizar la base de datos
 * Ejecutar este main para obtener los hashes
 */
public class PasswordHashGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("=== GENERADOR DE HASHES BCRYPT ===\n");

        // Clientes
        System.out.println("-- CLIENTES --");
        System.out.println("Password '123456': " + encoder.encode("123456"));
        System.out.println("Password 'cliente123': " + encoder.encode("cliente123"));

        // Veterinarios
        System.out.println("\n-- VETERINARIOS --");
        System.out.println("Password 'vet123': " + encoder.encode("vet123"));

        // Administradores
        System.out.println("\n-- ADMINISTRADORES --");
        System.out.println("Password 'admin123': " + encoder.encode("admin123"));

        System.out.println("\n=== FIN ===");
    }
}

