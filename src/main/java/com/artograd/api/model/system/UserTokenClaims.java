package com.artograd.api.model.system;

import com.artograd.api.model.enums.UserRole;
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

	private UserRole userRole = UserRole.ANONYMOUS_OR_CITIZEN;

}
