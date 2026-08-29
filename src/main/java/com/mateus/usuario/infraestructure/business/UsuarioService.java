package com.mateus.usuario.infraestructure.business;

import com.mateus.usuario.infraestructure.business.converter.UsuarioConverter;
import com.mateus.usuario.infraestructure.business.dto.EnderecoDTO;
import com.mateus.usuario.infraestructure.business.dto.TelefoneDTO;
import com.mateus.usuario.infraestructure.business.dto.UsuarioDTO;
import com.mateus.usuario.infraestructure.entity.Endereco;
import com.mateus.usuario.infraestructure.entity.Telefone;
import com.mateus.usuario.infraestructure.entity.Usuario;
import com.mateus.usuario.infraestructure.exceptions.ConflictException;
import com.mateus.usuario.infraestructure.exceptions.ResourceNotFoundException;
import com.mateus.usuario.infraestructure.security.JwtUtil;
import com.mateus.usuario.repository.EnderecoRepository;
import com.mateus.usuario.repository.TelefoneRepository;
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
    private final EnderecoRepository enderecoRepository;
    private final TelefoneRepository telefoneRepository;


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


    public UsuarioDTO buscarUsuarioPorEmail (String email){
        try {
            return usuarioConverter.paraUsuarioDTO(
                    usuarioRepository.findByEmail(email).orElseThrow(
                            () -> new ResourceNotFoundException("E-mail não encontrado" + email)));

        }catch (ResourceNotFoundException e){
            throw new ResourceNotFoundException("E-mail não encontrado" + email);
        }

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

    public EnderecoDTO atualizaEndereco (Long idEndereco, EnderecoDTO enderecoDTO){
        Endereco entity = enderecoRepository.findById(idEndereco).orElseThrow(() -> new ResourceNotFoundException("Id do endereço não encontrado" + idEndereco));

        Endereco endereco = usuarioConverter.updateEndereco(enderecoDTO, entity);

        return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(endereco));
    }

    public TelefoneDTO atualizaTelefone (Long idTelefone, TelefoneDTO telefoneDTO){
        Telefone entity = telefoneRepository.findById(idTelefone).orElseThrow(() -> new ResourceNotFoundException("Id do endereço não encontrado" + idTelefone));

        Telefone telefone = usuarioConverter.updateTelefone(telefoneDTO, entity);

        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefone));
    }

    public EnderecoDTO cadastraEndereco(String token, EnderecoDTO dto){
        String email = jwtUtil.extractUsername(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("E-mail não encontrado" + email));

        Endereco endereco = usuarioConverter.paraEnderecoEntity(dto, usuario.getId());
        Endereco enderecoEntity = enderecoRepository.save(endereco);

        return usuarioConverter.paraEnderecoDTO(enderecoEntity);

    }

    public TelefoneDTO cadastraTelefone(String token, TelefoneDTO dto){
        String email = jwtUtil.extractUsername(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("E-mail não encontrado" + email));

        Telefone telefone = usuarioConverter.paraTelefoneEntity(dto, usuario.getId());
        Telefone telefoneEntity = telefoneRepository.save(telefone);

        return usuarioConverter.paraTelefoneDTO(telefoneEntity);

    }







}
