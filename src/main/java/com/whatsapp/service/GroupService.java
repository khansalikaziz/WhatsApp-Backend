package com.whatsapp.service;

import com.whatsapp.dto.GroupDto;
import com.whatsapp.dto.MessageDto;
import com.whatsapp.dto.UserDto;
import com.whatsapp.model.Group;
import com.whatsapp.model.Message;
import com.whatsapp.model.User;
import com.whatsapp.repository.GroupRepository;
import com.whatsapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Transactional
    public GroupDto createGroup(GroupDto groupDto, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        Group group = new Group();
        group.setName(groupDto.getName());
        group.setDescription(groupDto.getDescription());
        group.setGroupIcon(groupDto.getGroupIcon());
        group.setCreatedBy(creator);

        group.getMembers().add(creator);
        group.getAdmins().add(creator);

        group = groupRepository.save(group);

        return mapToGroupDto(group, creatorId);
    }

    @Transactional
    public GroupDto addMember(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!group.getMembers().contains(user)) {
            group.getMembers().add(user);
            groupRepository.save(group);
        }

        return mapToGroupDto(group, userId);
    }

    @Transactional
    public void removeMember(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        group.getMembers().remove(user);
        group.getAdmins().remove(user);
        groupRepository.save(group);
    }

    @Transactional
    public void makeAdmin(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (group.getMembers().contains(user) && !group.getAdmins().contains(user)) {
            group.getAdmins().add(user);
            groupRepository.save(group);
        }
    }

    @Transactional
    public void removeAdmin(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (group.getAdmins().contains(user)) {
            group.getAdmins().remove(user);
            groupRepository.save(group);
        }
    }

    public List<GroupDto> getUserGroups(Long userId) {
        return groupRepository.findGroupsByUserId(userId).stream()
                .map(group -> mapToGroupDto(group, userId))
                .collect(Collectors.toList());
    }

    public GroupDto getGroupById(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        return mapToGroupDto(group, userId);
    }

    @Transactional
    public GroupDto updateGroup(Long groupId, GroupDto groupDto) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (groupDto.getName() != null) {
            group.setName(groupDto.getName());
        }
        if (groupDto.getDescription() != null) {
            group.setDescription(groupDto.getDescription());
        }
        if (groupDto.getGroupIcon() != null) {
            group.setGroupIcon(groupDto.getGroupIcon());
        }

        group = groupRepository.save(group);
        return mapToGroupDto(group, group.getCreatedBy().getId());
    }

    @Transactional
    public void deleteGroup(Long groupId) {
        groupRepository.deleteById(groupId);
    }

    public List<MessageDto> getGroupMessages(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        return group.getMessages().stream()
                .map(this::mapToMessageDto)
                .collect(Collectors.toList());
    }

    private GroupDto mapToGroupDto(Group group, Long currentUserId) {
        GroupDto dto = new GroupDto();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setDescription(group.getDescription());
        dto.setGroupIcon(group.getGroupIcon());
        dto.setCreatedById(group.getCreatedBy().getId());
        dto.setCreatedByName(group.getCreatedBy().getName());
        dto.setCreatedAt(group.getCreatedAt());

        dto.setMembers(group.getMembers().stream()
                .map(this::mapToUserDto).collect(Collectors.toList()));

        dto.setAdmins(group.getAdmins().stream()
                .map(this::mapToUserDto).collect(Collectors.toList()));

        if (!group.getMessages().isEmpty()) {
            Message lastMessage = group.getMessages().get(group.getMessages().size() - 1);
            dto.setLastMessage(lastMessage.getContent() != null ? lastMessage.getContent() : lastMessage.getMessageType().toString());
            dto.setLastMessageTime(lastMessage.getTimestamp());
        }

        long unreadCount = group.getMessages().stream()
                .filter(m -> !m.getSender().getId().equals(currentUserId)
                        && m.getStatus() != Message.MessageStatus.READ)
                .count();
        dto.setUnreadCount((int) unreadCount);

        return dto;
    }

    private UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setAbout(user.getAbout());
        dto.setOnline(user.isOnline());
        dto.setLastSeen(user.getLastSeen());
        return dto;
    }

    private MessageDto mapToMessageDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setSenderId(message.getSender().getId());
        dto.setTimestamp(message.getTimestamp());
        dto.setMessageType(message.getMessageType());
        dto.setMediaUrl(message.getMediaUrl());
        dto.setFileName(message.getFileName());
        dto.setStatus(message.getStatus());
        return dto;
    }
}
