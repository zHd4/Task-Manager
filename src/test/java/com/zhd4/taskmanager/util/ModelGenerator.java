package com.zhd4.taskmanager.util;

import com.zhd4.taskmanager.model.Label;
import com.zhd4.taskmanager.model.Task;
import com.zhd4.taskmanager.model.TaskStatus;
import com.zhd4.taskmanager.model.User;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ModelGenerator {
    private static final Faker FAKER = new Faker();

    private Model<User> userModel;
    private Model<TaskStatus> taskStatusModel;
    private Model<Task> taskModel;
    private Model<Label> labelModel;

    @PostConstruct
    private void init() {
        userModel = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getFirstName), () -> FAKER.name().firstName())
                .supply(Select.field(User::getLastName), () -> FAKER.name().lastName())
                .supply(Select.field(User::getEmail), () -> FAKER.internet().emailAddress())
                .supply(Select.field(User::getPassword), () -> FAKER.internet().password(5, 30))
                .toModel();

        taskStatusModel = Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .supply(Select.field(TaskStatus::getName), () -> FAKER.text().text(4, 7))
                .supply(Select.field(TaskStatus::getSlug), () -> FAKER.text().text(4, 7))
                .toModel();

        taskModel = Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .supply(Select.field(Task::getName), () -> FAKER.text().text(10, 20))
                .supply(Select.field(Task::getDescription), () -> FAKER.text().text())
                .toModel();

        labelModel = Instancio.of(Label.class)
                .ignore(Select.field(Label::getId))
                .supply(Select.field(Label::getName), () -> FAKER.text().text(3, 1000))
                .toModel();
    }
}
