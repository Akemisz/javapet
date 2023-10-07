package br.com.fiap.domain.repository;

import br.com.fiap.domain.entity.animal.Animal;
import br.com.fiap.domain.entity.pessoa.PF;
import br.com.fiap.domain.entity.pessoa.Pessoa;
import br.com.fiap.domain.service.PFService;
import br.com.fiap.infra.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class AnimalRepository implements Repository<Animal,Long>{

    public static final AtomicReference<AnimalRepository> instance = new AtomicReference<>();

    private AnimalRepository(){

    }

    public static AnimalRepository build() {

        AnimalRepository result = instance.get();
        if (Objects.isNull(result)) {
            AnimalRepository repo = new AnimalRepository();
            if (instance.compareAndSet(null, repo)) {
                result = repo;
            } else {
                result = instance.get();
            }
        }
        return result;
    }


    public List<Animal> findAll() {

        List<Animal> animais = new ArrayList<>();

        try {

            var factory = ConnectionFactory.build();
            Connection connection = factory.getConnection();

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery( "SELECT * FROM TB_ANIMAL" );

            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong( "ID_ANIMAL" );
                    String nome = resultSet.getString( "NM_ANIMAL" );
                    String raca = resultSet.getString("RACA");
                    String descricao = resultSet.getString("DS_ANIMAL");
                    Long dono = resultSet.getLong("DONO");


                    PFService pfService = new PFService();
                    PF pf = pfService.findById(dono);


                    String tipo = resultSet.getString("TP_ANIMAL");
                    //Adicionando equipamentos na coleção
                    animais.add(new Animal(id, nome, raca, descricao, pf, tipo) {
                    });
                }
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.err.println( "Não foi possivel consultar os dados!\n" + e.getMessage() );
        }
        return animais;
    }

    @Override
    public Animal findById(Long id) {
        Animal animal = null;
        var sql = "SELECT * FROM TB_ANIMAL where ID_ANIMAL=?";

        var factory = ConnectionFactory.build();
        Connection connection = factory.getConnection();



        try {
            PreparedStatement preparedStatement = connection.prepareStatement( sql );
            preparedStatement.setLong( 1, id );
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    animal = new Animal(
                            resultSet.getLong("ID_ANIMAL"),
                            resultSet.getString("NM_ANIMAL"),
                            resultSet.getString("RACA_ANIMAL"),
                            resultSet.getString("DS_ANIMAL"),
                            resultSet.getLong("DONO"),
                            resultSet.getString("TP_ANIMAL")
                    ) {
                    };
                }
            } else {
                System.out.println( "Animal não encontrado com o id = " + id );
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            System.err.println( "Não foi possível executar a consulta: \n" + e.getMessage() );
        }
        return animal;
    }

    @Override
    public Animal persiste(Animal animal) {
        var sql = "BEGIN" +
                " INSERT INTO animal (NM_ANIMAL) " +
                "VALUES (?) " +
                "returning ID_ANIMAL into ?; " +
                "END;" +
                "";


        var factory = ConnectionFactory.build();
        Connection connection = factory.getConnection();


        CallableStatement cs = null;
        try {
            cs = connection.prepareCall( sql );
            cs.setString( 1, animal.getNome());
            cs.registerOutParameter( 2, Types.BIGINT );
            cs.executeUpdate();
            animal.setId( cs.getLong( 2 ) );
            cs.close();
            connection.close();
        } catch (SQLException e) {
            System.err.println( "Não foi possível executar o comando!\n" + e.getMessage() );
        }
        return animal;
    }


}
