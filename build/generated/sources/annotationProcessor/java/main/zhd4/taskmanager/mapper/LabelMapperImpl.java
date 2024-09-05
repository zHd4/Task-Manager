package zhd4.taskmanager.mapper;

import zhd4.taskmanager.dto.LabelCreateDTO;
import zhd4.taskmanager.dto.LabelDTO;
import zhd4.taskmanager.dto.LabelUpdateDTO;
import zhd4.taskmanager.model.Label;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-08T18:24:08+0200",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.7.jar, environment: Java 19.0.2 (Amazon.com Inc.)"
)
@Component
public class LabelMapperImpl extends LabelMapper {

    @Autowired
    private JsonNullableMapper jsonNullableMapper;

    @Override
    public Label map(LabelCreateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Label label = new Label();

        label.setName( dto.getName() );

        return label;
    }

    @Override
    public LabelDTO map(Label model) {
        if ( model == null ) {
            return null;
        }

        LabelDTO labelDTO = new LabelDTO();

        labelDTO.setId( model.getId() );
        labelDTO.setName( model.getName() );
        labelDTO.setCreatedAt( model.getCreatedAt() );

        return labelDTO;
    }

    @Override
    public void update(LabelUpdateDTO dto, Label model) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getName() != null ) {
            model.setName( jsonNullableMapper.unwrap( dto.getName() ) );
        }
    }
}
