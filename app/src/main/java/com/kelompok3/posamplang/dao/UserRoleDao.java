package com.kelompok3.posamplang.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kelompok3.posamplang.models.UserRole;

import java.util.List;

@Dao
public interface UserRoleDao {
    @Insert
    long insert(UserRole userRole);

    @Update
    void update(UserRole userRole);

    @Delete
    void delete(UserRole userRole);

    @Query("SELECT * FROM user_roles")
    List<UserRole> getAll();

    @Query("SELECT * FROM user_roles WHERE user_roles_id = :id LIMIT 1")
    UserRole getById(int id);
}
