package br.com.fiap.domain.service;

import br.com.fiap.domain.entity.animal.Animal;
import br.com.fiap.domain.repository.AnimalRepository;
import br.com.fiap.domain.repository.PJRepository;

import java.util.List;

public class PJService implements Service<PJ,Long> {
    private PJRepository repo = PJRepository.build();


    @Override
    public List<PJ> findAll() {
        return repo.findAll();
    }

    @Override
    public PJ findById(Long id) {
        return null;
    }

    @Override
    public PJ persiste(PJ pj) {
        return null;
    }
}
