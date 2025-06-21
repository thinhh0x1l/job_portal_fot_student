package com.jobportal.entity;

import com.jobportal.model.TagType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String name;

    @Enumerated(EnumType.STRING)
    TagType tagType;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(id, tag.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /// tag-item bg-green-100 text-green-700 text-sm px-3 py-1 rounded-full cursor-pointer hover:bg-green-200 transition
    public String getColorClass() {
        return "inline-flex items-center px-3 py-1 rounded-full text-sm cursor-pointer transition"+
                switch (tagType) {
            case PROGRAMMING_LANGUAGE -> "bg-blue-100 text-blue-700 hover:bg-blue-200";
            case FRONTEND -> "bg-green-100 text-green-700 hover:bg-green-200";
            case BACKEND -> "bg-purple-100 text-purple-700 hover:bg-purple-200";
            case DATABASE -> "bg-pink-100 text-pink-700 hover:bg-pink-200";
            case OTHER -> "bg-yellow-100 text-yellow-700 hover:bg-yellow-200";
        };
    }
    public String getColorClassV1() {
        return "tag-item  text-sm px-3 py-1 rounded-full cursor-pointer transition "+
                switch (tagType) {
                    case PROGRAMMING_LANGUAGE -> "bg-blue-100 text-blue-700 hover:bg-blue-200";
                    case FRONTEND -> "bg-green-100 text-green-700 hover:bg-green-200";
                    case BACKEND -> "bg-purple-100 text-purple-700 hover:bg-purple-200";
                    case DATABASE -> "bg-pink-100 text-pink-700 hover:bg-pink-200";
                    case OTHER -> "bg-yellow-100 text-yellow-700 hover:bg-yellow-200";
                };
    }
    public String peerChecked() {
        return
                switch (tagType) {
                    case PROGRAMMING_LANGUAGE -> " peer-checked:bg-blue-600";
                    case FRONTEND -> " peer-checked:bg-green-600";
                    case BACKEND -> " peer-checked:bg-purple-600";
                    case DATABASE -> " peer-checked:bg-pink-600";
                    case OTHER -> " peer-checked:bg-yellow-600";
                };
    }
    public String peerCheckedV1() {
        return "peer-checked:text-white px-1 rounded-full transition " +
                switch (tagType) {
                    case PROGRAMMING_LANGUAGE -> " peer-checked:bg-blue-600";
                    case FRONTEND -> " peer-checked:bg-green-600";
                    case BACKEND -> " peer-checked:bg-purple-600";
                    case DATABASE -> " peer-checked:bg-pink-600";
                    case OTHER -> " peer-checked:bg-yellow-600";
                };
    }
}
