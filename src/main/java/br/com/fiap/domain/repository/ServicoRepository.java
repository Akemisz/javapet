package br.com.fiap.domain.repository;

import br.com.fiap.domain.entity.animal.Animal;
import br.com.fiap.domain.entity.pessoa.PF;
import br.com.fiap.domain.entity.servico.Servico;
import br.com.fiap.domain.service.AnimalService;
import br.com.fiap.infra.ConnectionFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ServicoRepository implements Repository<Servico,Long>{

    public static final AtomicReference<ServicoRepository> instance= new AtomicReference<>();

    private ServicoRepository(){

    }

    public static ServicoRepository build(){

        ServicoRepository result = instance.get();
        if(Objects.isNull(result)){
            ServicoRepository repo = new ServicoRepository();
            if(instance.compareAndSet(null,repo)){
                result = repo;
            } else {
                result = instance.get();
            }
        }
        return result;
    }


    @Override
    public List<Servico> findAll() {

        List<Servico> servicos= new ArrayList<>();

        try{

            var factory = ConnectionFactory.build();
            Connection connection = factory.getConnection();

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM TB_SERVICO");

            if (resultSet.isBeforeFirst()){
                while(resultSet.next()){
                    Long id = resultSet.getLong("ID_SERVICO");
                    String descricao = resultSet.getString("DS_SERVICO");
                    Long animal = resultSet.getLong("ANIMAL");
                    Date realizacao = resultSet.getDate("DT_REALIZACAO");
                    String tipo = resultSet.getString("DS_SERVICO");

                    AnimalService animalService = new AnimalService();
                    Animal animal = animalService.findById(animal);


                    servicos.add(new Servico(id,descricao,animal,realizacao,tipo) {
                    });
                }
            }
            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            System.err.println( "Não foi possivel consultar os dados!\n" + e.getMessage() );
        }
        return servicos;
    }

    @Override
    public Servico findById(Long id) {
        return null;
    }

    @Override
    public Servico persiste(Servico servico) {
        return null;
    }

}
