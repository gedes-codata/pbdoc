package br.gov.jfrj.siga.cp;

import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Guarda as informações de AAA (Authentication, Authorization and Accounting).
 * 
 * @author michel
 */
@Entity
@Table(name = "cp_aaa", schema = "corporativo")
public class AAA {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(mappedBy = "aaa")
	private Set<CpIdentidade> identidades;

	@Column(length = 64)
	private String senha;

	public AAA() {
	}

	public AAA(String senha) {
		this();
		this.senha = senha;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<CpIdentidade> getIdentidades() {
		return identidades;
	}

	public void setIdentidades(Set<CpIdentidade> identidades) {
		this.identidades = identidades;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	@Override
	public int hashCode() {
		return Objects.hash(senha);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AAA other = (AAA) obj;
		return Objects.equals(senha, other.senha);
	}

}
