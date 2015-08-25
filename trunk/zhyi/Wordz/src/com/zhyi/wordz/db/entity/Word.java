/*
 * Word.java
 *
 * Copyright (C) 2010 Zhao Yi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.zhyi.wordz.db.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Maps WORD table in the database.
 */
@Entity
@NamedQueries({
    @NamedQuery(name = Word.FIND_ALL_BY_ALPHABETICAL_ORDER, query = "select w from Word w order by w.spelling"),
    @NamedQuery(name = Word.FIND_ALL_BY_CREATED_TIME, query = "select w from Word w order by w.createdTime desc"),
    @NamedQuery(name = Word.COUNT, query = "select count(w) from Word w"),
    @NamedQuery(name = Word.SEARCH_BY_ALPHABETICAL_ORDER, query = "select w from Word w where w.spelling like ?1 order by w.spelling"),
    @NamedQuery(name = Word.SEARCH_BY_CREATED_TIME, query = "select w from Word w where w.spelling like ?1 order by w.createdTime desc"),
    @NamedQuery(name = Word.SEARCH_COUNT, query = "select count(w) from Word w where w.spelling like ?1"),
    @NamedQuery(name = Word.GET_INDEX_BY_ALPHABETICAL_ORDER, query = "select count(w) from Word w where w.spelling < ?1"),
    @NamedQuery(name = Word.GET_INDEX_BY_CREATED_TIME, query = "select count(w) from Word w where w.createdTime > ?1")
})
public class Word implements Serializable {

    public static final String FIND_ALL_BY_ALPHABETICAL_ORDER = "Word.findAllByAlphabeticalOrder";
    public static final String FIND_ALL_BY_CREATED_TIME = "Word.findAllByCreatedTime";
    public static final String COUNT = "Word.count";
    public static final String SEARCH_BY_ALPHABETICAL_ORDER = "Word.searchByAlphabeticalOrder";
    public static final String SEARCH_BY_CREATED_TIME = "Word.searchByCreatedTime";
    public static final String SEARCH_COUNT = "Word.searchCount";
    public static final String GET_INDEX_BY_ALPHABETICAL_ORDER = "Word.indexByAlphabeticalOrder";
    public static final String GET_INDEX_BY_CREATED_TIME = "Word.indexByCreatedTime";

    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false, length = 32)
    private String spelling;
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false, length = 32)
    private String pronunciation;
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false, length = 64)
    private String explanation;
    @Embedded
    @ElementCollection
    @OrderBy("index")
    private List<Example> examples;
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSpelling() {
        return spelling;
    }

    public void setSpelling(String spelling) {
        this.spelling = spelling;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public List<Example> getExamples() {
        return examples;
    }

    public void setExamples(List<Example> examples) {
        this.examples = examples;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Word)) {
            return false;
        }
        Word other = (Word) object;
        if (id == null || other.id == null) {
            return false;
        }
        return id.equals(other.id);
    }

    @Override
    public String toString() {
        return spelling;
    }

}
