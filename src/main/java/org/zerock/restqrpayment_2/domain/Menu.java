package org.zerock.restqrpayment_2.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
// restaurant 제외
@ToString(exclude = "restaurant")
@Entity
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(length = 500)
    private String description;

    @Column(length = 50)
    private String menuCategory;

    @ElementCollection
    @CollectionTable(name = "menu_file_names", joinColumns = @JoinColumn(name = "menu_id"))
    @Column(name = "file_name")
    @Builder.Default
    private List<String> fileNames = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Restaurant restaurant;

    @OneToMany(mappedBy = "menu",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Builder.Default
    private Set<MenuImage> imageSet = new HashSet<>();

    public void changeMenu(String name, Double price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public void setMenuCategory(String menuCategory) {
        this.menuCategory = menuCategory;
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public void addMenuImage(String uuid, String fileName) {
        MenuImage menuImage = MenuImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .menu(this)
                .build();
        imageSet.add(menuImage);
    }

    public void clearMenuImages() {
        imageSet.clear();
    }
}
