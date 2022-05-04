@file:OptIn(ExperimentalComposeUiApi::class)

package com.example.tipcalculatorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tipcalculatorapp.components.InputField
import com.example.tipcalculatorapp.ui.theme.TipCalculatorAppTheme
import com.example.tipcalculatorapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                MyApp {
                    Column() {
                        MainContent()

                    }


                }
        }
    }
}



@Composable
fun MyApp(content: @Composable () -> Unit){
    TipCalculatorAppTheme {
         Surface(
            color = MaterialTheme.colors.background) {
                content()

            }
    }
}

@Preview
@Composable
fun TopHeader(totalPerPerson: Double =0.0 ){
        Surface(modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .height(150.dp)
            .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
        color = Color(0xFFE9D7f7)) {
            Column(modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {

                val total = "%.2f".format(totalPerPerson)

                Text(text = "Total Per Person",
                style = MaterialTheme.typography.h5)
                Text(text = "$$total",
                style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.ExtraBold)

            }

    }

}

@Preview
@Composable
fun MainContent() {
        Billform(){
            billAmout ->
        }
}

@Composable
fun Billform(modifier: Modifier = Modifier,
            onValChange: (String) -> Unit = {}){

    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val sliderPositionState = remember() {
        mutableStateOf(0f)
    }
    val tipAmountstate = remember {
        mutableStateOf(0.0)
    }
    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }
    val tipPercentage = (sliderPositionState.value *100).toInt()
    val splitByState = remember {
        mutableStateOf(1)
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    Column {
        TopHeader(totalPerPerson = totalPerPersonState.value)

        Surface(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
            shape = RoundedCornerShape(corner = CornerSize(8.dp)),
            border = BorderStroke(1.dp, color = Color.LightGray)
        ) {

            Column(modifier = Modifier.padding(6.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top){
                InputField(valueState =totalBillState,
                    labelId ="Enter Bill",
                    enabled =true,
                    isSingleLine =true,
                    onAction = KeyboardActions{
                        if (!validState) return@KeyboardActions
                        onValChange(totalBillState.value.trim())
                        keyboardController?.hide()

                    })
                if(validState){
                    Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.Start) {

                        Text(text = "Split", modifier = Modifier.align(
                            alignment = Alignment.CenterVertically
                        ))
                        Spacer(modifier = Modifier.width(120.dp))

                        Row(modifier = Modifier,
                            horizontalArrangement = Arrangement.End) {

                            RoundIconButton(imageVector = Icons.Default.Remove, onClick = {

                                splitByState.value =
                                    if(splitByState.value >1)
                                        splitByState.value -1
                                    else 1
                                totalPerPersonState.value = calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByState.value,
                                    tipPercentage = tipPercentage)
                            })

                            Text(text = "${splitByState.value}", modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(horizontal = 9.dp))

                            RoundIconButton(imageVector = Icons.Default.Add, onClick = {
                                splitByState.value =
                                    if(splitByState.value <100)
                                        splitByState.value +1
                                    else 1
                                totalPerPersonState.value = calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByState.value,
                                    tipPercentage = tipPercentage)
                            })
                        }


                    }
                    Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)) {

                        Text(text = "Tip", modifier = Modifier.align(alignment = Alignment.CenterVertically))
                        Spacer(modifier = Modifier.width(200.dp))
                        Text(text = "${tipAmountstate.value}", modifier = Modifier.align(alignment = Alignment.CenterVertically))


                    }

                    Column(verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {

                        Text(text = "$tipPercentage%")
                        Spacer(modifier = Modifier.height(14.dp))

                        Slider(value = sliderPositionState.value,

                            onValueChange = {
                                    newVal -> sliderPositionState.value = newVal
                                tipAmountstate.value =
                                    calculateTotalTip(totalBill = totalBillState.value.toDouble(), tipPercentage = tipPercentage)

                                totalPerPersonState.value = calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByState.value,
                                    tipPercentage = tipPercentage
                                )

                            }, modifier = Modifier.padding(horizontal = 16.dp),
                            steps = 5,
                        )


                    }


                }else{
                    Box() {

                    }
                }
            }

        }
    }



}

fun calculateTotalTip(totalBill: Double, tipPercentage: Int): Double {
            return  if(totalBill >1  &&
                    totalBill.toString().isNotEmpty())
                (totalBill* tipPercentage) /100 else 0.0
}

fun calculateTotalPerPerson(
    totalBill: Double,
    splitBy: Int,
    tipPercentage: Int): Double{

    val bill = calculateTotalTip(totalBill = totalBill,
                                tipPercentage = tipPercentage,)+ totalBill
    return  (bill / splitBy)
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {

        MyApp {

            Column {
                TopHeader()

                MainContent()
            }

        }

}