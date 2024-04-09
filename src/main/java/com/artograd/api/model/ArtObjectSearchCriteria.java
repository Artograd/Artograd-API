package com.artograd.api.model;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ArtObjectSearchCriteria {
    
	private String title;
	
    private List<String> locationLeafIds;
    
    private List<String> statuses;
    
    private String userId;
    
    private int page = 0;
    
    private int size = 10;
    
    private String sortBy = "createdAt";
    
    private String sortOrder = "desc";
}

