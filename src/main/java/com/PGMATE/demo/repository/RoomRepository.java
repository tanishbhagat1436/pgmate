package com.PGMATE.demo.repository;

import com.PGMATE.demo.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    List<Room> findByPgPgId(Integer pgId);

    List<Room> findByPgPgIdAndStatus(Integer pgId, Room.RoomStatus status);
}