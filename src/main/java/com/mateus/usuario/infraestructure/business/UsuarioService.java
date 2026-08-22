package com.mateus.usuario.infraestructure.business;

import com.mateus.usuario.infraestructure.business.converter.UsuarioConverter;
import com.mateus.usuario.infraestructure.business.dto.UsuarioDTO;
import com.mateus.usuario.infraestructure.entity.Usuario;
import com.mateus.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;


    //Recebemos o usuario DTO
    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO){
        //Convertemos para usuario Entity
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        //Salvamos usuario como entity no banco
        usuario = usuarioRepository.save(usuario);
        //depois converte novamente para DTO e retorna
        return usuarioConverter.paraUsuarioDTO(usuario);

    }

}
