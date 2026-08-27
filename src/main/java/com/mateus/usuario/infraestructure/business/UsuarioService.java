package com.mateus.usuario.infraestructure.business;

import com.mateus.usuario.infraestructure.business.converter.UsuarioConverter;
import com.mateus.usuario.infraestructure.business.dto.UsuarioDTO;
import com.mateus.usuario.infraestructure.entity.Usuario;
import com.mateus.usuario.infraestructure.exceptions.ConflictException;
import com.mateus.usuario.infraestructure.exceptions.ResourceNotFoundException;
import com.mateus.usuario.infraestructure.security.JwtUtil;
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
    private final JwtUtil jwtUtil;


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


    public Usuario buscarUsuarioPorEmail (String email){
        return usuarioRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("E-mail não encontrado" + email));
    }

    public void deletarUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioDTO atualizaDadosUsuario(UsuarioDTO dto){

        return dto;
    }

    public UsuarioDTO atualizaUsuario(String token, UsuarioDTO dto){
        // busca email do usuario atraves do token para retirar obrigatoriedade de passar email
        String email = jwtUtil.extractUsername(token.substring(7));
        //Criptografia de senha
        dto.setSenha(dto.getSenha() != null ? passwordEncoder.encode(dto.getSenha()) : null);

        // busca os dados de usuario no banco
        Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("E-mail não localizado!! "));
        // mesclou os dados recebidos na requisicao DTO com os dados do banco
        Usuario usuario = usuarioConverter.updateUsuario(dto, usuarioEntity);
        // salvou os dados do usuario convertido e depois pegou o retorno e converteu para usuarioDTO
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }




    

}
