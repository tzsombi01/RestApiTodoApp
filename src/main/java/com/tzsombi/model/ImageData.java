package com.tzsombi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "ta_images")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ImageData implements Serializable {

    @Id
    @SequenceGenerator(
            name = "ta_images_sequence",
            sequenceName=  "ta_images_sequence",
            allocationSize = 10
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ta_images_sequence"
    )
    @Column(name = "image_id", updatable = false, nullable = false)
    private Long imageId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Lob
    @Column(name = "image_data", length = 1000, nullable = false)
    private byte[] imageData;

    @OneToOne(mappedBy = "image")
    @JsonManagedReference
    private User user;
}
