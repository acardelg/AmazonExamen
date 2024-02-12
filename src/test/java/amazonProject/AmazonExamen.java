package amazonProject;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;



public class AmazonExamen {
	
	private WebDriver driver;
	
	@Before
	//Accede a Google con el navegador Chrome 
	public void configura() {
		System.setProperty("webdriver.chrome.driver", "./drivers/chromedriver_121.0.6167.85.exe");
		driver= new ChromeDriver();
		driver.manage().window().maximize();
		driver.get("https://www.google.es/");
	}
	@Test
	public void test() throws InterruptedException  {
		//Declaración de producto y otras variables
		String product="compresor electrico";
		String mainTab =driver.getWindowHandle();
		String newTab="";
		JavascriptExecutor js = (JavascriptExecutor) driver;
		
		//Aceptar cookies de Google
		driver.findElement(By.id("L2AGLb")).click();
		
		//Busca un producto (por ejemplo, compresor eléctrico) en el buscador de Google
		driver.findElement(By.xpath("//textarea[@aria-controls='Alh6id']")).sendKeys(product);
		driver.findElement(By.xpath("//textarea[@aria-controls='Alh6id']")).sendKeys(Keys.ENTER);
		
		//En los resultados accede al enlace de Amazon. Se añade un try/catch por sino fuera posible.
        WebElement clickProduct = null;
		try {
			clickProduct= driver.findElement(By.xpath("//span[@aria-label='De Amazon.es' and @class='zPEcBd VZqTOd']"));
		js.executeScript("arguments[0].click();", clickProduct);
		Actions actions = new Actions(driver);
		actions.moveToElement(clickProduct).click().perform();
		}catch (Exception e) {
            System.out.println("No se pudo hacer clic en el enlace de Amazon: " + e.getMessage());
		}
		
		//Cambiar el foco a la nueva ventana. Se añade try/catch si esta operacion falla
		try {
			Set<String> handles =driver.getWindowHandles();
			for (String actual:handles) {
				if(!actual.equalsIgnoreCase(mainTab)) {
					driver.switchTo().window(actual);
					newTab=actual;
				}
			}
		}catch (Exception e) {
			System.out.println("No se ha podido abrir la nueva ventana" + e.getMessage());
		}
		
		//Se valida que se ha accedido a la página correcta
		 String currentUrl = driver.getCurrentUrl();
	     assert currentUrl.contains("amazon"); 
		
		//Se hace click en las Cookies de Amazon
		driver.findElement(By.id("a-autoid-0")).click();

		//Imprime por pantalla el precio y la fecha de entrega
		String price1 = driver.findElement(By.xpath("//span[@class='a-price-whole']")).getText();
		String price2 = driver.findElement(By.xpath("//span[@class='a-price-fraction']")).getText();
		
		// Encuentra el elemento span que contiene el atributo data-csa-c-delivery-time
        WebElement spanElement = driver.findElement(By.cssSelector("span[data-csa-c-delivery-time]"));
        
        // Obtiene el valor del atributo data-csa-c-delivery-time del elemento span
        String deliveryTime = spanElement.getAttribute("data-csa-c-delivery-time");
        
        // Imprime los valores
        System.out.println("Fecha de entrega: " + deliveryTime);
		System.out.println("El precio es " + price1 +"," +price2 + " EUR");
		
		//En el buscador general vuelve a buscar el producto 
		driver.findElement(By.id("twotabsearchtextbox")).sendKeys("compresor electrico");
		driver.findElement(By.id("twotabsearchtextbox")).sendKeys(Keys.ENTER);
		
		//Filtra por entregas prime 
		driver.findElement(By.xpath("//div[@class='a-checkbox a-checkbox-fancy s-navigation-checkbox aok-float-left']")).click();
		
		//Ordena de precio más bajo a mas alto
		Thread.sleep(1000);
		driver.findElement(By.xpath("//span[@class='a-button a-button-dropdown a-button-small']")).click();
		WebElement searchOptions=driver.findElement(By.id("s-result-sort-select_1"));
		js.executeScript("arguments[0].click();", searchOptions);	
		 
		//Imprime por pantalla el nombre de los productos de la primera pagina y el precio, solo imprime los productos que se han encontrado no los sugeridos, ni las búsquedas antiguas. 
		Thread.sleep(1000);
		List<WebElement> productos=  driver.findElements(By.xpath("//h2[@class='a-size-mini a-spacing-none a-color-base s-line-clamp-4']"));
		List<WebElement> priceInt = driver.findElements(By.xpath("//span[@class='a-price-whole']"));
		List<WebElement> priceDec = driver.findElements(By.xpath("//span[@class='a-price-fraction']"));
		if(productos.isEmpty()) {
				productos=driver.findElements(By.xpath("//h2[@class='a-size-mini a-spacing-none a-color-base s-line-clamp-2']"));	
		}
        System.out.println("Lista de productos y precios:");
		Integer n=1;
		for (int i = 0; i < productos.size(); i++) {
			String p = productos.get(i).getText();
		    String pi = priceInt.get(i).getText();
		    String pd = priceDec.get(i).getText();
		    System.out.println(n +".  " + p + "  " + pi + "," + pd +" EUR");
		    n++;
		}		    
		//Cierra todas las ventanas
		driver.quit();			
		
	}
}



