package io.yaoyuan.doingcms.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Options.
 */
@Entity
@Table(name = "options")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Options implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "option_name", nullable = false)
    private String optionName;

    @NotNull
    @Column(name = "option_value", nullable = false)
    private String optionValue;

    @NotNull
    @Column(name = "autoload", nullable = false)
    private String autoload;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Options id(Long id) {
        this.id = id;
        return this;
    }

    public String getOptionName() {
        return this.optionName;
    }

    public Options optionName(String optionName) {
        this.optionName = optionName;
        return this;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public String getOptionValue() {
        return this.optionValue;
    }

    public Options optionValue(String optionValue) {
        this.optionValue = optionValue;
        return this;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    public String getAutoload() {
        return this.autoload;
    }

    public Options autoload(String autoload) {
        this.autoload = autoload;
        return this;
    }

    public void setAutoload(String autoload) {
        this.autoload = autoload;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Options)) {
            return false;
        }
        return id != null && id.equals(((Options) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Options{" +
            "id=" + getId() +
            ", optionName='" + getOptionName() + "'" +
            ", optionValue='" + getOptionValue() + "'" +
            ", autoload='" + getAutoload() + "'" +
            "}";
    }
}
