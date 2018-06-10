/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupo02.negocio.oferta;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import br.com.grupo02.negocio.IGerenciarDados;
import br.com.grupo02.negocio.departamento.Departamento;
import br.com.grupo02.negocio.disciplina.Disciplina;
import br.com.grupo02.negocio.error.ConexaoException;
import br.com.grupo02.negocio.error.DAOException;
import br.com.grupo02.negocio.professor.Professor;
import br.com.grupo02.persistencia.GerenciadorConexao;
import br.com.grupo02.persistencia.GerenciarConexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *Implementação dos métodos CRUD com otimizacao utlizando stringBuilder
 * @author Bruno Rodrigues /Git: @Brunojgrc
 */
public class OfertaDAO implements IGerenciarDados <Oferta> {
    
    /**
    * Método responsável por inserir dados na tabela oferta;
    * @param oferta objeto que será inserido quando esse método for chamado;
     * 
    */
    
    @Override
    public void inserir(Oferta oferta) throws ConexaoException,DAOException {
        
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO Oferta ")
        .append("(horario,id_disciplina,id_professor )")
        .append("VALUES")
        .append("(?,?,?)");
            
        int i = 1;        
        try (Connection con = GerenciarConexao.getInstancia().conectar()){
            PreparedStatement pst; 
            pst = con.prepareStatement(sb.toString());
            pst.setDate(i++, oferta.getData());
            pst.setInt(i++, oferta.getDisciplina().getId());
            pst.setInt(i++, oferta.getProfessor().getId());
            pst.executeUpdate();
            pst.close();
            
        }catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
        }finally {
            sb.delete(0, sb.length());
            sb = null;     
        }
    }

   @Override
     public void atualizar(Oferta oferta) throws ConexaoException, DAOException {
        GerenciadorConexao gc;
        gc = GerenciarConexao.getInstancia();
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE OFERTA SET ");
        sb.append("id_disciplina=?, id_professor=?, horario=? ");
        sb.append("WHERE");
        sb.append(" id=?");
        String sql = sb.toString().trim();
        PreparedStatement pst;
        int i = 1;
        try (Connection con = gc.conectar()) {
            pst = con.prepareStatement(sql);
            pst.setInt(i++, oferta.getId());
            pst.setDate(i++, oferta.getData());
            pst.setInt(i++, oferta.getDisciplina().getId());
            pst.setInt(i++, oferta.getProfessor().getId());
            pst.execute();
            pst.close();
            pst.executeUpdate();
            
            
        } catch (Exception e) {
            throw new DAOException();
        } finally {
            sb.delete(0, sb.length());
            sb = null;    
        }
    }
     
     @Override
    public void deletar(Integer id) throws ConexaoException, DAOException {
        
        GerenciadorConexao gc;
        gc = GerenciarConexao.getInstancia();
        String sql = "DELETE FROM OFERTA WHERE id =? ";
        PreparedStatement pst = null;
        try (Connection con = gc.conectar()) {
            pst = con.prepareStatement(sql);
            pst.setInt(1,id);
            pst.execute();
            pst.close();
        } catch (Exception e) {
            throw new DAOException();
        }
    }
    
    @Override
    public Oferta buscarPorId(Integer id) throws ConexaoException, DAOException {
        
        GerenciadorConexao gc;
        gc = GerenciarConexao.getInstancia();
        String sql = "SELECT * FROM OFERTA WHERE id=" + id;
        try (Connection con = gc.conectar()) {
            try (Statement st = con.createStatement()){
                ResultSet rs = st.executeQuery(sql);          
                
                if(rs.next()){
                    Oferta oferta = new Oferta();
                    oferta.setId(rs.getInt("id"));
                    //oferta.getDisciplina().setId(rs.getInt("id_disciplina"));
                    //oferta.getProfessor().setId(rs.getInt("id_professor"));
                    oferta.setData(rs.getDate("horario"));
                    
                    /*Disciplina disciplina = new Disciplina();
                    disciplina.setId(rs.getInt("id_disciplina"));
                    oferta.setDisciplina (disciplina );
                    
                    Professor professor = new Professor();
                    professor.setId(rs.getInt("id_professor"));
                    oferta.setProfessor (professor);
                    */                
                    return oferta;
                }   
            }
            
                 
        } catch (SQLException ex) { 
            throw new DAOException();
        } 
       
        return oferta;
    }   

     public List<Oferta> listarTodos() throws ConexaoException, DAOException {
        GerenciadorConexao gc;
        gc = GerenciarConexao.getInstancia();
        List<Oferta> lista = new ArrayList();
        Oferta oferta = null;
        String sql = "SELECT * FROM OFERTA";
        try (Connection con = gc.conectar()) {
            try (Statement stm = con.createStatement()) {
                ResultSet rs = stm.executeQuery(sql);
                while (rs.next()) {
                    //montando o objeto disciplina com o resultado da consulta do banco
                oferta = new Oferta ();
                oferta.setId(rs.getInt("id") );
                oferta.setData( rs.getDate("horario") );
                Disciplina disciplina = new Disciplina();
                disciplina.setId(rs.getInt("id_disciplina"));
                oferta.setDisciplina(disciplina);
                lista.add(oferta);
                }   
            return lista;
            }

        } catch (Exception e) {
            throw new DAOException();
        }

    }
   
}
