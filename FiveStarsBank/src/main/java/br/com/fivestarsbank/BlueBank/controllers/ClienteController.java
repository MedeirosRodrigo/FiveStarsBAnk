package br.com.fivestarsbank.BlueBank.controllers;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.fivestarsbank.BlueBank.models.Cliente;
import br.com.fivestarsbank.BlueBank.models.DTO.ClienteDTO;
import br.com.fivestarsbank.BlueBank.models.DTO.ClienteDTO2;
import br.com.fivestarsbank.BlueBank.service.ClienteService;
import br.com.fivestarsbank.BlueBank.service.SNSEmailService;

@RestController
@RequestMapping(path = "/clientes")
public class ClienteController {
 
	@Autowired
	private ClienteService service;
	@Autowired
	private SNSEmailService sns_service;

	@GetMapping(path = "/{id}")
	public ResponseEntity<Cliente> buscar(@PathVariable Long id) {
		Cliente cliente = service.buscar(id);
		return ResponseEntity.ok().body(cliente);
	}
	
	@GetMapping
	public ResponseEntity<Page<Cliente>> listar(Pageable pageable) {
		Page<Cliente> listaCliente = service.listarPage(pageable);
		return ResponseEntity.ok().body(listaCliente);
	}

	@PostMapping
	public ResponseEntity<String> cadastrar(@Valid @RequestBody ClienteDTO cliente) {
		Cliente cli = service.cadastrar(cliente);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(cli.getId()).toUri();

		String body = "Cadastro realizado com sucesso! \nConfirmação pendente, verifique seu e-mail: "
				+ cli.getContatos().get(0).getEmail();
		return ResponseEntity.created(uri).body(body);
	}

	@GetMapping(path = "/snsEmail/{id}") 
	public ResponseEntity<String> enviarEmail(@PathVariable Long id) {
		Cliente cli = service.buscar(id);
		sns_service.enviarEmail(cli.getTopico(), service.mensagemEmail(), "Five Stars Bank");
		String body = "E-mail enviado com sucesso!";
		return ResponseEntity.ok().body(body);
	}

	@PutMapping(path = "/{id}")
	public ResponseEntity<Void> atualizar(@PathVariable Long id, @Valid @RequestBody ClienteDTO2 cliente) {
		service.atualizar(cliente, id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping(path = "/{id}")
	public ResponseEntity<Void> deletar(@PathVariable Long id) {
		service.deletar(id);
		return ResponseEntity.noContent().build();
	}

}
