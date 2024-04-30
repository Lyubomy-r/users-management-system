package com.rest.java.test.task;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;


import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SpringBootTest
//@AutoConfigureMockMvc
class JavaTestApplicationTests {

	@Test
	void contextLoads() {
	}

//	@Autowired
//	private MockMvc mockMvc;
//
//	@Autowired
//	private UsersController usersController;

//	@Test
//	public void contextLoads() throws Exception{
//		assertThat(usersController).isNotNull();
//	}
//	@Test
//	public void contextLoads() throws Exception {
//
//		this.mockMvc.perform(get("/users/search2")
//						.andDo(print()))
//				.andExpect(status().isOk())
//				.andExpect(content().string(containsString("BD : " + from + " BD" + to));
//	}
}


