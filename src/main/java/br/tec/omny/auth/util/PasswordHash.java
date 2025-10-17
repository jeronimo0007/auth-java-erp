package br.tec.omny.auth.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * Classe para hash de senhas compatível com a implementação PHP
 * Baseada na classe PasswordHash do PHP
 */
public class PasswordHash {
    
    private final String itoa64 = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final int iterationCountLog2;
    private final Random random;
    
    /**
     * Construtor
     * @param iterationCountLog2 Número de iterações (4-31)
     * @param portableHashes Se deve usar hashes portáveis (não utilizado na implementação atual)
     */
    public PasswordHash(int iterationCountLog2, boolean portableHashes) {
        if (iterationCountLog2 < 4 || iterationCountLog2 > 31) {
            iterationCountLog2 = 8;
        }
        this.iterationCountLog2 = iterationCountLog2;
        this.random = new SecureRandom();
    }
    
    /**
     * Gera bytes aleatórios
     * @param count Número de bytes
     * @return Array de bytes aleatórios
     */
    private byte[] getRandomBytes(int count) {
        byte[] output = new byte[count];
        random.nextBytes(output);
        return output;
    }
    
    /**
     * Codifica em base64 customizada
     * @param input Array de bytes
     * @param count Número de bytes para codificar
     * @return String codificada
     */
    private String encode64(byte[] input, int count) {
        StringBuilder output = new StringBuilder();
        int i = 0;
        
        do {
            int value = input[i++] & 0xFF;
            output.append(itoa64.charAt(value & 0x3f));
            
            if (i < count) {
                value |= (input[i] & 0xFF) << 8;
            }
            output.append(itoa64.charAt((value >> 6) & 0x3f));
            
            if (i++ >= count) {
                break;
            }
            
            if (i < count) {
                value |= (input[i] & 0xFF) << 16;
            }
            output.append(itoa64.charAt((value >> 12) & 0x3f));
            
            if (i++ >= count) {
                break;
            }
            
            output.append(itoa64.charAt((value >> 18) & 0x3f));
        } while (i < count);
        
        return output.toString();
    }
    
    /**
     * Gera salt privado
     * @param input Array de bytes
     * @return String do salt
     */
    private String gensaltPrivate(byte[] input) {
        StringBuilder output = new StringBuilder("$P$");
        // PHP usa +5 quando PHP_VERSION >= '5', senão usa +3
        // Como estamos em Java (equivalente a PHP 5+), sempre usamos +5
        output.append(itoa64.charAt(Math.min(iterationCountLog2 + 5, 30)));
        output.append(encode64(input, 6));
        return output.toString();
    }
    
    /**
     * Criptografia privada
     * @param password Senha
     * @param setting Configuração do salt
     * @return Hash da senha
     */
    private String cryptPrivate(String password, String setting) {
        String output = "*0";
        
        if (setting.startsWith(output)) {
            output = "*1";
        }
        
        String id = setting.substring(0, 3);
        if (!id.equals("$P$") && !id.equals("$H$")) {
            return output;
        }
        
        int countLog2 = itoa64.indexOf(setting.charAt(3));
        if (countLog2 < 7 || countLog2 > 30) {
            return output;
        }
        
        int count = 1 << countLog2;
        String salt = setting.substring(4, 12);
        
        if (salt.length() != 8) {
            return output;
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            // PHP: $hash = md5($salt . $password, true);
            byte[] hash = md.digest((salt + password).getBytes());
            
            // PHP: do { $hash = md5($hash . $password, true); } while (--$count);
            for (int i = 0; i < count; i++) {
                md.reset();
                // Concatena hash + password (não apenas hash)
                byte[] combined = new byte[hash.length + password.getBytes().length];
                System.arraycopy(hash, 0, combined, 0, hash.length);
                System.arraycopy(password.getBytes(), 0, combined, hash.length, password.getBytes().length);
                hash = md.digest(combined);
            }
            
            output = setting.substring(0, 12);
            output += encode64(hash, 16);
            
        } catch (NoSuchAlgorithmException e) {
            return "*";
        }
        
        return output;
    }
    
    /**
     * Gera hash da senha
     * @param password Senha em texto plano
     * @return Hash da senha
     */
    public String hashPassword(String password) {
        byte[] randomBytes = getRandomBytes(6);
        String hash = cryptPrivate(password, gensaltPrivate(randomBytes));
        
        if (hash.length() == 34) {
            return hash;
        }
        
        return "*";
    }
    
    /**
     * Verifica se a senha está correta
     * @param password Senha em texto plano
     * @param storedHash Hash armazenado
     * @return true se a senha estiver correta
     */
    public boolean checkPassword(String password, String storedHash) {
        String hash = cryptPrivate(password, storedHash);
        
        if (hash.charAt(0) == '*') {
            // Fallback para outros métodos de hash (equivalente ao crypt() do PHP)
            hash = cryptFallback(password, storedHash);
        }
        
        return hash.equals(storedHash);
    }
    
    /**
     * Método fallback para verificação de senha (equivalente ao crypt() do PHP)
     * @param password Senha em texto plano
     * @param storedHash Hash armazenado
     * @return Hash gerado
     */
    private String cryptFallback(String password, String storedHash) {
        // Se o hash armazenado começa com $2a$, $2b$ ou $2y$, é bcrypt
        if (storedHash.startsWith("$2a$") || storedHash.startsWith("$2b$") || storedHash.startsWith("$2y$")) {
            return cryptBcrypt(password, storedHash);
        }
        
        // Se o hash armazenado começa com $1$, é MD5 tradicional
        if (storedHash.startsWith("$1$")) {
            return cryptMD5(password, storedHash);
        }
        
        // Se o hash armazenado começa com $5$, é SHA-256
        if (storedHash.startsWith("$5$")) {
            return cryptSHA256(password, storedHash);
        }
        
        // Se o hash armazenado começa com $6$, é SHA-512
        if (storedHash.startsWith("$6$")) {
            return cryptSHA512(password, storedHash);
        }
        
        // Se não reconhecer o formato, retorna o hash original
        return storedHash;
    }
    
    /**
     * Implementação de bcrypt usando Spring Security
     * @param password Senha
     * @param storedHash Hash armazenado
     * @return Hash gerado
     */
    private String cryptBcrypt(String password, String storedHash) {
        try {
            // Usa o BCrypt do Spring Security para verificar a senha
            if (BCrypt.checkpw(password, storedHash)) {
                return storedHash;
            } else {
                return "*";
            }
        } catch (Exception e) {
            return storedHash;
        }
    }
    
    /**
     * Implementação de MD5 tradicional
     * @param password Senha
     * @param storedHash Hash armazenado
     * @return Hash gerado
     */
    private String cryptMD5(String password, String storedHash) {
        try {
            // Extrai o salt do hash armazenado
            String[] parts = storedHash.split("\\$");
            if (parts.length < 3) {
                return storedHash;
            }
            
            String salt = parts[2];
            if (salt.length() > 8) {
                salt = salt.substring(0, 8);
            }
            
            // Gera o hash MD5 com salt
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest((password + salt).getBytes());
            
            // Converte para hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return "$1$" + salt + "$" + hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            return storedHash;
        }
    }
    
    /**
     * Implementação de SHA-256
     * @param password Senha
     * @param storedHash Hash armazenado
     * @return Hash gerado
     */
    private String cryptSHA256(String password, String storedHash) {
        try {
            // Extrai o salt do hash armazenado
            String[] parts = storedHash.split("\\$");
            if (parts.length < 3) {
                return storedHash;
            }
            
            String salt = parts[2];
            if (salt.length() > 16) {
                salt = salt.substring(0, 16);
            }
            
            // Gera o hash SHA-256 com salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest((password + salt).getBytes());
            
            // Converte para base64
            return "$5$" + salt + "$" + java.util.Base64.getEncoder().encodeToString(hash);
            
        } catch (NoSuchAlgorithmException e) {
            return storedHash;
        }
    }
    
    /**
     * Implementação de SHA-512
     * @param password Senha
     * @param storedHash Hash armazenado
     * @return Hash gerado
     */
    private String cryptSHA512(String password, String storedHash) {
        try {
            // Extrai o salt do hash armazenado
            String[] parts = storedHash.split("\\$");
            if (parts.length < 3) {
                return storedHash;
            }
            
            String salt = parts[2];
            if (salt.length() > 16) {
                salt = salt.substring(0, 16);
            }
            
            // Gera o hash SHA-512 com salt
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hash = md.digest((password + salt).getBytes());
            
            // Converte para base64
            return "$6$" + salt + "$" + java.util.Base64.getEncoder().encodeToString(hash);
            
        } catch (NoSuchAlgorithmException e) {
            return storedHash;
        }
    }
    
    /**
     * Método de teste para verificar compatibilidade
     * @param args Argumentos da linha de comando
     */
    public static void main(String[] args) {
        PasswordHash ph = new PasswordHash(8, false);
        
        // Teste com senha simples
        String password = "teste123";
        String hash = ph.hashPassword(password);
        
        System.out.println("Senha: " + password);
        System.out.println("Hash gerado: " + hash);
        System.out.println("Verificação: " + ph.checkPassword(password, hash));
        
        // Teste com hash que começa com *
        String invalidHash = "*0";
        System.out.println("Teste com hash inválido: " + ph.checkPassword(password, invalidHash));
        
        // Teste com diferentes formatos de hash
        String[] testHashes = {
            "$1$salt123$hash123",
            "$2a$10$salt123456789012345678901234567890123456789012345678901234567890",
            "$5$salt123456789012$hash123",
            "$6$salt123456789012$hash123"
        };
        
        for (String testHash : testHashes) {
            System.out.println("Teste com " + testHash + ": " + ph.checkPassword(password, testHash));
        }
    }
}
