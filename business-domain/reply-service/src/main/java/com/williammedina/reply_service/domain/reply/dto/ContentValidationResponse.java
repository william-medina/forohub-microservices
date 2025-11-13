package com.williammedina.reply_service.domain.reply.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de la validación de contenido mediante IA. Indica si el contenido es aprobado o si contiene algún problema.")
public record ContentValidationResponse(

        @Schema( description = "Resultado de la validación del contenido. Puede ser 'approved' si el contenido es adecuado, o un mensaje iniciando con 'contiene' describiendo el problema.", example = "approved")
        String result
) {
}
