package dao;

import model.Client;

import java.util.List;

public interface ClientDao {
    List<Client> findAll();

    Client findById(int idClient);

    void insert(Client client);

    void update(Client client);

    void deleteById(int idClient);

    List<Client> searchByName(String nameQuery);
}
