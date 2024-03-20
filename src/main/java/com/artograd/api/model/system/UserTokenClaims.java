package com.artograd.api.model.system;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserTokenClaims {

	private String username;

	private boolean isOfficer;

	private boolean isArtist;

}
