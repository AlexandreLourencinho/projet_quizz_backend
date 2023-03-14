package fr.loual.projectquizz.security.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class DeleteRequestConfirmation {

    private boolean deleteRequest;
    private boolean confirmedDeleteRequest;

}
