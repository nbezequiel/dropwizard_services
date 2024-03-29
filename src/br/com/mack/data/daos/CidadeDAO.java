/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mack.data.daos;

import br.com.mack.data.DataAccess;
import br.com.mack.data.TDAO;
import br.com.mack.domain.Cidade;
import br.com.mack.domain.Estado;
import br.com.mack.domain.Pais;
import br.com.mack.exception.DatabaseCommandException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author ezequiel
 */
public class CidadeDAO implements TDAO<Cidade> {

    private Connection conn;
    private DataAccess database;
    
    public CidadeDAO(DataAccess dbAccess) throws ClassNotFoundException{
        this.database=dbAccess;
        this.conn=dbAccess.getConnection();
    }
    
    public ResultSet query(PreparedStatement stm) throws SQLException, ClassNotFoundException{
        ResultSet rs=stm.executeQuery();
        return rs;
    }
    
    public int execute(PreparedStatement stm) throws SQLException, ClassNotFoundException{
        
        int retorno=stm.executeUpdate();
        return retorno;
    }
    
    
    @Override
    public int insert(Cidade cidade) throws Exception{
        int retorno=0;
        String sqlCommand="INSERT INTO Cidade (nome,estado,pais,populacao) values(?,?,?,?)";
        
        PreparedStatement stm=conn.prepareStatement(sqlCommand);
        stm.setString(1,cidade.getNome());
        stm.setInt(2,cidade.getEstado().getId());
        stm.setInt(3,cidade.getPais().getId());
        stm.setInt(4,cidade.getPopulacao());
        
        try {
            retorno=execute(stm);
        } 
        catch (Exception ex) {
            System.out.println("Erro ao executar o comando sql"+ex.getMessage());
        }
               
        stm.close();
        
        return retorno;
    }

    @Override
    public int update(Cidade cidade,Long id) throws Exception {
        int retorno=0;
        String sqlCommand="Update Cidade set nome=?, estado=?, pais=?, populacao=? where id=?";

        PreparedStatement stm=conn.prepareStatement(sqlCommand);
        stm.setString(1,cidade.getNome());
        stm.setInt(2,cidade.getEstado().getId());
        stm.setInt(3,cidade.getPais().getId());
        stm.setInt(4,cidade.getPopulacao());
        stm.setLong(5,id);
            
        try {
            retorno=execute(stm);
        } 
        catch (Exception ex) {
            throw new DatabaseCommandException("Erro ao executar o comando sql"+ex.getMessage());
        }
               
        stm.close();
        
        return retorno;
    }
    @Override
    public Cidade get(int id) throws Exception {
        Cidade cidade=null;
        String sqlCommand="select * from Cidade where id=?";
        PreparedStatement stm=conn.prepareStatement(sqlCommand);
        stm.setInt(1,id);
        
        try {
            ResultSet rs=query(stm);
            
            while(rs.next()){
                String nome=rs.getString("nome");
                Estado estado=new Estado(rs.getInt("estado"));
                Pais pais=new Pais(rs.getInt("pais"));
                int populacao=rs.getInt("populacao");
                cidade =new Cidade(id,nome,estado,pais,populacao);
            }
        } 
        catch (Exception ex) {
            throw new DatabaseCommandException("Erro ao executar o comando sql"+ex.getMessage());
        }
       
        stm.close();
        
        return cidade;
    }
    
    @Override
    public List<Cidade> toList() throws Exception {
        List<Cidade> cidades=null;
        String sqlCommand="select * from Cidade";
        try (PreparedStatement stm = conn.prepareStatement(sqlCommand)) {
            try {
                ResultSet rs= query(stm);
                cidades=new ArrayList();
                
                while(rs.next()){
                    int id=rs.getInt("id");
                    String nome=rs.getString("nome");
                    Estado estado=new Estado(rs.getInt("estado"));
                    Pais pais=new Pais(rs.getInt("pais"));
                    int populacao=rs.getInt("populacao");
                    cidades.add(new Cidade(id,nome,estado,pais,populacao));
                }
            }
            catch (Exception ex) {
                throw new DatabaseCommandException("Erro ao executar o comando sql"+ex.getMessage());
            }
            stm.close();
        }
        return cidades;
    }

    
    public List<Cidade> listCatalog(String param,String value,int lastId) throws SQLException, DatabaseCommandException {
        String sqlCommand="";
        PreparedStatement stm ;
       sqlCommand="select c.id,c.nome,c.populacao, e.nome, p.nome from "
                + "Cidade as c inner join Pais as p on p.id=c.pais inner join Estado as e "
                + "on e.id=c.estado  where c.id >? and c."+param+" like ? limit 6";
            stm= conn.prepareStatement(sqlCommand);
            stm.setInt(1, lastId);
            stm.setString(2,"%"+value+"%");
        
        List<Cidade> cidades=null;
        
        try {
            ResultSet rs= query(stm);
            cidades=new ArrayList();
            
            while(rs.next()){
                int id=rs.getInt("id");
                String nome=rs.getString("nome");
                Pais pais=new Pais(0,rs.getString("p.nome"));
                Estado estado=new Estado(0,rs.getString("e.nome"),pais);
                int populacao=rs.getInt("populacao");
                cidades.add(new Cidade(id,nome,estado,pais,populacao));
            }
        }
        catch (Exception ex) {
            throw new DatabaseCommandException("Erro ao executar o comando sql"+ex.getMessage());
        }
        stm.close();
        return cidades;
    }
    
    @Override
    public int delete(Long id) throws SQLException, DatabaseCommandException {
        int retorno=0;
        String sqlCommand="delete from Cidade where id=?";
        
        PreparedStatement stm=conn.prepareStatement(sqlCommand);
        stm.setLong(1,id);
        
        try {
            retorno=execute(stm);
        } 
        catch (Exception ex) {
            throw new DatabaseCommandException("Erro ao executar o comando sql"+ex.getMessage());
        }
        
        stm.close();
        
        return retorno;
    }
    
    public List<Estado> listEstados(int idPais) throws SQLException {
        List<Estado> estados=null;
        String sqlCommand="select * from Estado where pais=?";
        PreparedStatement stm=conn.prepareStatement(sqlCommand); 
        stm.setInt(1,idPais);
        
        try {
            ResultSet rs=query(stm);
            estados=new ArrayList();
            
            while(rs.next()){
                int id=rs.getInt("id");
                String nome=rs.getString("nome");
                Pais pais=new Pais(idPais);
                estados.add(new Estado(id,nome,pais));
            }
        } 
        catch (Exception ex) {
            System.out.println("Erro ao Buscar "+ex.getMessage());
        }
               
        stm.close();
        return estados;
    }
    
    public List<Pais> listPaises() throws SQLException {
        List<Pais> paises=null;
        String sqlCommand="select * from Pais";
        PreparedStatement stm=conn.prepareStatement(sqlCommand); 

        try {
            ResultSet rs= rs=query(stm);
            paises=new ArrayList();
            
            while(rs.next()){
                int id=rs.getInt("id");
                String nome=rs.getString("nome");
                paises.add(new Pais(id,nome));
            }
        } 
        catch (Exception ex) {
            System.out.println("Erro ao Buscar "+ex.getMessage());
        }
               
        stm.close();
        
        return paises;
    }
    
    public Pais getPais(int id) throws SQLException {
        Pais pais=null;
        String sqlCommand="select * from Pais where id=?";
        PreparedStatement stm=conn.prepareStatement(sqlCommand); 
        stm.setInt(1,id);

        try {
            ResultSet rs= rs=query(stm);
           
            while(rs.next()){
                String nome=rs.getString("nome");
                pais=new Pais(id,nome);
            }
            
        } 
        catch (Exception ex) {
            System.out.println("Erro ao Buscar "+ex.getMessage());
        }
               
        stm.close();
        
        return pais;
    }
    
    public Estado getEstado(int id) throws SQLException, DatabaseCommandException {
        Estado estado=null;
        String sqlCommand="select * from Estado where id=?";
        PreparedStatement stm=conn.prepareStatement(sqlCommand); 
        stm.setInt(1,id);

        try {
            ResultSet rs= rs=query(stm);
            rs.next();
        
            String nome=rs.getString("nome");
            Pais pais=new Pais(rs.getInt("pais"));        
            estado=new Estado(id,nome,pais);
        } 
        catch (Exception ex) {
            throw new DatabaseCommandException("Erro ao executar o comando sql"+ex.getMessage());
        }
               
        stm.close();
        
        return estado;
    }
}
