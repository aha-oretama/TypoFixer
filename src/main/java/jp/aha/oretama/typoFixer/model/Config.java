package jp.aha.oretama.typoFixer.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author aha-oretama
 */
@Data
public class Config {
    private List<String> extensions = new ArrayList<>();
}
