package com.rembli.ums;
import lombok.*;
import javax.xml.bind.annotation.*;

@Data
@XmlRootElement

public class UserInfo {
	protected String username;
	protected String email;
}
