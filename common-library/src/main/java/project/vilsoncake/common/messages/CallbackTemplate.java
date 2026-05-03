package project.vilsoncake.common.messages;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallbackTemplate {
  private String buttonText;
  private String messageTemplate;
  private Map<String, String> buttons;
}
