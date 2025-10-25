package com.whatsapp.controller;

import com.whatsapp.dto.GroupDto;
import com.whatsapp.service.FileStorageService;
import com.whatsapp.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final FileStorageService fileStorageService;

    @PostMapping
    public ResponseEntity<GroupDto> createGroup(
            @RequestBody GroupDto groupDto,
            @RequestParam Long creatorId) {
        GroupDto created = groupService.createGroup(groupDto, creatorId);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GroupDto>> getUserGroups(@PathVariable Long userId) {
        List<GroupDto> groups = groupService.getUserGroups(userId);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDto> getGroupById(
            @PathVariable Long groupId,
            @RequestParam Long userId) {
        GroupDto group = groupService.getGroupById(groupId, userId);
        return ResponseEntity.ok(group);
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<GroupDto> updateGroup(
            @PathVariable Long groupId,
            @RequestBody GroupDto groupDto) {
        GroupDto updated = groupService.updateGroup(groupId, groupDto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{groupId}/icon")
    public ResponseEntity<String> uploadGroupIcon(
            @PathVariable Long groupId,
            @RequestParam("file") MultipartFile file) {
        String fileUrl = fileStorageService.storeFile(file, "groups");
        GroupDto groupDto = new GroupDto();
        groupDto.setGroupIcon(fileUrl);
        groupService.updateGroup(groupId, groupDto);
        return ResponseEntity.ok(fileUrl);
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<GroupDto> addMember(
            @PathVariable Long groupId,
            @RequestParam Long userId) {
        GroupDto updated = groupService.addMember(groupId, userId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long userId) {
        groupService.removeMember(groupId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{groupId}/admins/{userId}")
    public ResponseEntity<Void> makeAdmin(
            @PathVariable Long groupId,
            @PathVariable Long userId) {
        groupService.makeAdmin(groupId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long groupId) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.ok().build();
    }
}
