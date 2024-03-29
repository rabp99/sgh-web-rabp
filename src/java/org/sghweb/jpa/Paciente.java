/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sghweb.jpa;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Roberto
 */
@Entity
@Table(name = "paciente")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Paciente.findAll", query = "SELECT p FROM Paciente p"),
    @NamedQuery(name = "Paciente.findByDni", query = "SELECT p FROM Paciente p WHERE p.dni = :dni"),
    @NamedQuery(name = "Paciente.findByNombreCompleto", query = "SELECT p FROM Paciente p WHERE p.nombreCompleto = :nombreCompleto"),
    @NamedQuery(name = "Paciente.findByUbicacionNacimiento", query = "SELECT p FROM Paciente p WHERE p.ubicacionNacimiento = :ubicacionNacimiento"),
    @NamedQuery(name = "Paciente.findByUbicacionActual", query = "SELECT p FROM Paciente p WHERE p.ubicacionActual = :ubicacionActual"),
    @NamedQuery(name = "Paciente.findByDireccion", query = "SELECT p FROM Paciente p WHERE p.direccion = :direccion"),
    @NamedQuery(name = "Paciente.findByGenero", query = "SELECT p FROM Paciente p WHERE p.genero = :genero"),
    @NamedQuery(name = "Paciente.findByFechaNacimiento", query = "SELECT p FROM Paciente p WHERE p.fechaNacimiento = :fechaNacimiento"),
    @NamedQuery(name = "Paciente.findByTipoAsegurado", query = "SELECT p FROM Paciente p WHERE p.tipoAsegurado = :tipoAsegurado"),
    @NamedQuery(name = "Paciente.findByEstadoCivil", query = "SELECT p FROM Paciente p WHERE p.estadoCivil = :estadoCivil"),
    @NamedQuery(name = "Paciente.findByTitular", query = "SELECT p FROM Paciente p WHERE p.titular = :titular"),
    @NamedQuery(name = "Paciente.findByEstado", query = "SELECT p FROM Paciente p WHERE p.estado = :estado"),
    @NamedQuery(name = "Paciente.findByUsername", query = "SELECT p FROM Paciente p WHERE p.username = :username"),
    @NamedQuery(name = "Paciente.findByPassword", query = "SELECT p FROM Paciente p WHERE p.password = :password"),
    @NamedQuery(name = "Paciente.findByUsernameYPassword", query = "SELECT p FROM Paciente p WHERE p.username = :username AND p.password = :password")})
public class Paciente implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 8)
    @Column(name = "dni")
    private String dni;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "nombreCompleto")
    private String nombreCompleto;
    @Size(max = 45)
    @Column(name = "ubicacionNacimiento")
    private String ubicacionNacimiento;
    @Size(max = 45)
    @Column(name = "ubicacionActual")
    private String ubicacionActual;
    @Size(max = 100)
    @Column(name = "direccion")
    private String direccion;
    @Column(name = "genero")
    private Character genero;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fechaNacimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;
    @Size(max = 45)
    @Column(name = "tipoAsegurado")
    private String tipoAsegurado;
    @Column(name = "estadoCivil")
    private Character estadoCivil;
    @Size(max = 15)
    @Column(name = "titular")
    private String titular;
    @Column(name = "estado")
    private Character estado;
    @Size(max = 45)
    @Column(name = "username")
    private String username;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "password")
    private String password;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "paciente")
    private List<Historiaclinica> historiaclinicaList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "paciente")
    private List<Referencia> referenciaList;

    public Paciente() {
    }

    public Paciente(String dni) {
        this.dni = dni;
    }

    public Paciente(String dni, String nombreCompleto, Date fechaNacimiento, String password) {
        this.dni = dni;
        this.nombreCompleto = nombreCompleto;
        this.fechaNacimiento = fechaNacimiento;
        this.password = password;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getUbicacionNacimiento() {
        return ubicacionNacimiento;
    }

    public void setUbicacionNacimiento(String ubicacionNacimiento) {
        this.ubicacionNacimiento = ubicacionNacimiento;
    }

    public String getUbicacionActual() {
        return ubicacionActual;
    }

    public void setUbicacionActual(String ubicacionActual) {
        this.ubicacionActual = ubicacionActual;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Character getGenero() {
        return genero;
    }

    public void setGenero(Character genero) {
        this.genero = genero;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTipoAsegurado() {
        return tipoAsegurado;
    }

    public void setTipoAsegurado(String tipoAsegurado) {
        this.tipoAsegurado = tipoAsegurado;
    }

    public Character getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(Character estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public Character getEstado() {
        return estado;
    }

    public void setEstado(Character estado) {
        this.estado = estado;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @XmlTransient
    public List<Historiaclinica> getHistoriaclinicaList() {
        return historiaclinicaList;
    }

    public void setHistoriaclinicaList(List<Historiaclinica> historiaclinicaList) {
        this.historiaclinicaList = historiaclinicaList;
    }

    @XmlTransient
    public List<Referencia> getReferenciaList() {
        return referenciaList;
    }

    public void setReferenciaList(List<Referencia> referenciaList) {
        this.referenciaList = referenciaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dni != null ? dni.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Paciente)) {
            return false;
        }
        Paciente other = (Paciente) object;
        if ((this.dni == null && other.dni != null) || (this.dni != null && !this.dni.equals(other.dni))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.sghweb.jpa.Paciente[ dni=" + dni + " ]";
    }
    
}
