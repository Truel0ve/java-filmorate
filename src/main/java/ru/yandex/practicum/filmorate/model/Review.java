package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
public class Review {
    private Long reviewId;
    private String content; //Описание отзыва
    private Boolean isPositive; //Отзыв - позитивный или негативный (какой - решает юзер, оставивший отзыв)
    private Long userId; //id юзера
    private Long filmId; //id фильма, к которому ставится отзыв
    private Long useful; //Расчетное поле, по умолчанию равно нулю, лайк увеличивает на +1, дизлайк - уменьшает на -1

}
