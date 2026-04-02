package com.skystat.taf.domain.vo.taf;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.Accessors;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;

@Getter @Accessors(fluent = true)
@EqualsAndHashCode
public final class TafId  {

  private final String value;

  private TafId(String value) {
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException("TafId value cannot be blank.");
    }
    this.value = value;
  }

  public static TafId of(String hashedValue) {
    if (hashedValue == null || hashedValue.isBlank()) {
      throw new IllegalArgumentException("Hashed value cannot be blank.");
    }
    return new TafId(hashedValue.trim().toLowerCase(Locale.ROOT));
  }

  public static TafId fromReportText(String reportText) {
    String normalized = normalize(reportText);
    return new TafId(sha256(normalized));
  }


  private static String normalize(String reportText) {
    return reportText.trim().replaceAll("\\s+", " ").toUpperCase();
  }

  private static String sha256(String value) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 algorithm is not available.", e);
    }
  }

}
