package com.twd.SpringSecurityJWT.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TeamMemberRequestTest {

    @Test
    public void testGettersAndSetters() {
        // Arrange
        TeamMemberRequest teamMemberRequest = new TeamMemberRequest();

        // Act
        teamMemberRequest.setUserId(123);

        // Assert
        assertThat(teamMemberRequest.getUserId()).isEqualTo(123);
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        TeamMemberRequest teamMemberRequest1 = new TeamMemberRequest();
        teamMemberRequest1.setUserId(123);

        TeamMemberRequest teamMemberRequest2 = new TeamMemberRequest();
        teamMemberRequest2.setUserId(123);

        // Act & Assert
        // Default `equals` compares object references, not field values
        assertThat(teamMemberRequest1).isNotEqualTo(teamMemberRequest2);

        // Default `hashCode` is based on object reference, not field values
        assertThat(teamMemberRequest1.hashCode()).isNotEqualTo(teamMemberRequest2.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        TeamMemberRequest teamMemberRequest = new TeamMemberRequest();
        teamMemberRequest.setUserId(123);

        // Act
        String toStringResult = teamMemberRequest.toString();

        // Assert
        // Default `toString` includes the class name and hash code, not field values
        assertThat(toStringResult).doesNotContain("userId=123");
    }
}