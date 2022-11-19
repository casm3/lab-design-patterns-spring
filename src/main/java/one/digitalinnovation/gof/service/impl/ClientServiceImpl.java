package one.digitalinnovation.gof.service.impl;

import one.digitalinnovation.gof.model.Address;
import one.digitalinnovation.gof.model.AddressRepository;
import one.digitalinnovation.gof.model.Client;
import one.digitalinnovation.gof.model.ClientRepository;
import one.digitalinnovation.gof.service.ClientService;
import one.digitalinnovation.gof.service.ViaCepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ViaCepService viaCepService;
    @Override
    public Iterable<Client> searchAll() {
        return clientRepository.findAll();
    }

    @Override
    public Client searchById(Long id) {
        // Buscar Cliente por ID.
        Optional<Client> client = clientRepository.findById(id);
        return client.get();
    }

    @Override
    public void insert(Client client) {
        fillClient(client, client.getAddress().getCep());
    }

    private void fillClient(Client client, String postalCode) {
        Address address = addressRepository.findById(postalCode).orElseGet(() -> {
            Address newAddress = viaCepService.checkPostalCode(postalCode);
            addressRepository.save(newAddress);
            return newAddress;
        });
        client.setAddress(address);
        clientRepository.save(client);
    }

    @Override
    public void update(Long id, Client client) {
        Optional<Client> clientDb = clientRepository.findById(id);
        if (clientDb.isPresent()) {
            fillClient(client, client.getAddress().getCep());
        }
    }

    @Override
    public void delete(Long id) {
        clientRepository.deleteById(id);
    }
}
