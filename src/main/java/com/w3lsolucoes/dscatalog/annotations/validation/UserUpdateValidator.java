package com.w3lsolucoes.dscatalog.annotations.validation;

import com.w3lsolucoes.dscatalog.controllers.handlers.FieldMessage;
import com.w3lsolucoes.dscatalog.dto.UserDTO;
import com.w3lsolucoes.dscatalog.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserDTO> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpServletRequest request;

    @Override
    public void initialize(UserUpdateValid ann) {
    }

    @Override
    public boolean isValid(UserDTO dto, ConstraintValidatorContext context) {

        // Recupera o ID do usuário do path variable da requisição
        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables =
                (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Long userId = Long.parseLong(pathVariables.get("id"));

        List<FieldMessage> list = new ArrayList<>();

        // Verifica se já existe um usuário com o mesmo email (que não seja ele mesmo)
        var user = userRepository.findByEmail(dto.email());
        if (user != null && !user.getId().equals(userId)) {
            list.add(new FieldMessage("email", "Email já existe"));
        }

        // Adiciona violações de validação, se houver
        for (FieldMessage e : list) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage())
                    .addPropertyNode(e.getFieldName())
                    .addConstraintViolation();
        }

        return list.isEmpty();
    }
}
