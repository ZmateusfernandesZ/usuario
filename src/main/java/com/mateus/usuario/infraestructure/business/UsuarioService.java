package com.mateus.usuario.infraestructure.business;

import com.mateus.usuario.infraestructure.business.converter.UsuarioConverter;
import com.mateus.usuario.infraestructure.business.dto.UsuarioDTO;
import com.mateus.usuario.infraestructure.entity.Usuario;
import com.mateus.usuario.infraestructure.exceptions.ConflictException;
import com.mateus.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;


    //Recebemos o usuario DTO
    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO){
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        //Convertemos para usuario Entity
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        //Salvamos usuario como entity no banco
        usuario = usuarioRepository.save(usuario);
        //depois converte novamente para DTO e retorna
        return usuarioConverter.paraUsuarioDTO(usuario);

    }


    public void emailExiste(String email){
        try {
            boolean existe = verificaEmailExistente(email);
            if (existe){
                throw new ConflictException("Email ja cadastrado" + email);
            }
        }catch (ConflictException e){
            throw new ConflictException("Email ja cadastrado" + e.getCause());
        }
    }

    public boolean verificaEmailExistente (String email){

        return usuarioRepository.existsByEmail(email);
    }

}
