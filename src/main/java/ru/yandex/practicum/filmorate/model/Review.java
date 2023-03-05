package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
public class Review {
    private Long id;
    @NotBlank
    private String content;
    @NotNull
    private Boolean isPositive; //Отзыв - позитивный или негативный (какой - решает юзер, оставивший отзыв)
    @NotNull
    @Positive
    private Long userId; //id юзера
    @NotNull
    @Positive
    private Long filmId; //id фильма, к которому ставится отзыв
    private Long useful; //Расчетное поле, по умолчанию равно нулю, зависит от лайков и дизлайков

}
