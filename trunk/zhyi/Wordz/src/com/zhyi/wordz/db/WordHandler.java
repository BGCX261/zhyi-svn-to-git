/*
 * WordHandler.java
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
package com.zhyi.wordz.db;

import com.zhyi.wordz.db.entity.Word;
import com.zhyi.wordz.common.Context;
import com.zhyi.wordz.common.WordOrder;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * Handles DB-related operations on word.
 */
public class WordHandler {

    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static EntityTransaction et;

    /**
     * Connects the database.
     * @throws Exception If any error occurred.
     */
    public static void connectDataBase() throws Exception {
        System.setProperty("derby.system.home", Context.WORDZ_HOME);
        emf = Persistence.createEntityManagerFactory("WordzPU");
        em = emf.createEntityManager();
        et = em.getTransaction();
    }

    /**
     * Disconnects the database.
     * @throws Exception If any error occurred.
     */
    public static void disconnectDataBase() throws Exception {
        em.close();
        emf.close();
    }

    /**
     * Counts how many words there are in the database with an optional key.
     * @param key The key for searching words.
     * @return Counts all words if {@code key} is {@code null}, otherwise
     * counts how many words contain {@code key}.
     * @throws Exception If any error occurred.
     */
    public static long getWordCount(String key) throws Exception {
        // EclipseLink forgot to implement this method?
//        return  em.createNamedQuery(Word.COUNT, Long.class).getSingleResult();
        return key == null ?
            (Long) em.createNamedQuery(Word.COUNT).getSingleResult() :
            (Long) em.createNamedQuery(Word.SEARCH_COUNT).setParameter(1, key).getSingleResult();
    }

    /**
     * Retrieves words from database.
     * @param firstResult The position of the first result to retrieve. 
     * @param maxResults The maximum number of results to retrieve.
     * @param wordOrder The order of retrieved words.
     * @return The retrieved words.
     * @throws Exception If any error occurred.
     */
    public static List<Word> getWords(int firstResult, int maxResults,
            WordOrder wordOrder) throws Exception {
        // EclipseLink forgot to implement this method?
//        return em.createNamedQuery(Word.FIND_ALL, Word.class).getResultList();
        switch (wordOrder) {
            case ALPHABETICAL_ORDER:
                return em.createNamedQuery(Word.FIND_ALL_BY_ALPHABETICAL_ORDER)
                        .setFirstResult(firstResult).setMaxResults(maxResults)
                        .getResultList();
            case CREATED_TIME:
            default:
                return em.createNamedQuery(Word.FIND_ALL_BY_CREATED_TIME)
                        .setFirstResult(firstResult).setMaxResults(maxResults)
                        .getResultList();
        }
    }

    /**
     * Retrieves words from database with a specified searching key.
     * @param key The key for searching.
     * @param firstResult The position of the first result to retrieve.
     * @param maxResults The maximum number of results to retrieve.
     * @param wordOrder The order of retrieved words.
     * @return The retrieves words.
     * @throws Exception If any error occurred.
     */
    public static List<Word> searchWords(String key, int firstResult,
            int maxResults, WordOrder wordOrder) throws Exception {
        switch (wordOrder) {
            case ALPHABETICAL_ORDER:
                return em.createNamedQuery(Word.SEARCH_BY_ALPHABETICAL_ORDER)
                        .setParameter(1, "%" + key + "%").setFirstResult(firstResult)
                        .setMaxResults(maxResults).getResultList();
            case CREATED_TIME:
            default:
                return em.createNamedQuery(Word.SEARCH_BY_CREATED_TIME)
                        .setParameter(1, "%" + key + "%").setFirstResult(firstResult)
                        .setMaxResults(maxResults).getResultList();
        }
    }

    /**
     * Gets the position of a word in the query result.
     * @param word The word.
     * @param wordOrder The order for querying words.
     * @return The position.
     * @throws Exception If any error occurred.
     */
    public static long getPosition(Word word, WordOrder wordOrder) throws Exception {
        switch (Context.getWordOrder()) {
            case ALPHABETICAL_ORDER:
                return (Long) em.createNamedQuery(Word.GET_INDEX_BY_ALPHABETICAL_ORDER)
                        .setParameter(1, word.getSpelling()).getSingleResult();
            case CREATED_TIME:
            default:
                return (Long) em.createNamedQuery(Word.GET_INDEX_BY_CREATED_TIME)
                        .setParameter(1, word.getCreatedTime()).getSingleResult();
        }
    }

    /**
     * Saves a new word, or updates an existing word to the database.
     * @param word The word to save or update.
     * @throws Exception If any error occurred.
     */
    public static void saveWord(Word word) throws Exception {
        et.begin();
        Integer id = word.getId();
        if (id == null) {
            em.persist(word);
        } else {
            em.merge(word);
        }
        et.commit();
    }

    /**
     * Deletes words from database.
     * @param words Words to be deleted.
     * @throws Exception If any error occurred.
     */
    public static void deleteWords(List<Word> words) throws Exception {
        et.begin();
        for (Word word : words) {
            em.remove(word);
        }
        et.commit();
    }

}
